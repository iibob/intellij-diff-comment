package com.github.iibob.diffcomment;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DiffCommentAction extends DumbAwareAction {

    // 扩展名 → 行注释符号
    private static final Map<String, String> COMMENT_MAP = new HashMap<>();

    static {
        // Python / Shell / Ruby / YAML / Perl / R
        for (String ext : new String[]{"py", "sh", "bash", "zsh", "rb", "yaml", "yml", "pl", "pm", "r"}) {
            COMMENT_MAP.put(ext, "#");
        }
        // C / C++ / Java / Kotlin / Swift / Dart / Go / Rust / Scala / JS / TS / CSS(近似)
        for (String ext : new String[]{"c", "cpp", "cc", "cxx", "h", "hpp",
                "java", "kt", "kts", "swift", "dart", "go",
                "rs", "scala", "js", "jsx", "ts", "tsx",
                "cs", "m", "mm", "groovy", "php"}) {
            COMMENT_MAP.put(ext, "//");
        }
        // SQL
        for (String ext : new String[]{"sql"}) {
            COMMENT_MAP.put(ext, "--");
        }
        // Lua
        COMMENT_MAP.put("lua", "--");
        // HTML / XML（降级处理，用 <!-- --> 复杂，改为不操作）
        // Haskell / Elm
        for (String ext : new String[]{"hs", "elm"}) {
            COMMENT_MAP.put(ext, "--");
        }
        // MATLAB / Octave
        COMMENT_MAP.put("m", "%");   // 会被上面 ObjC 的 .m 覆盖，按需调整
        // Fortran
        for (String ext : new String[]{"f", "f90", "f95", "for"}) {
            COMMENT_MAP.put(ext, "!");
        }
        // TOML / INI / Properties / Makefile
        for (String ext : new String[]{"toml", "ini", "cfg", "conf", "properties", "mk"}) {
            COMMENT_MAP.put(ext, "#");
        }
        // VimScript
        COMMENT_MAP.put("vim", "\"");
        // Assembly
        for (String ext : new String[]{"asm", "s"}) {
            COMMENT_MAP.put(ext, ";");
        }
        // Dockerfile（无扩展名，特殊处理见下方）
        COMMENT_MAP.put("dockerfile", "#");
        // Batch
        for (String ext : new String[]{"bat", "cmd"}) {
            COMMENT_MAP.put(ext, "REM ");
        }
        // PowerShell
        COMMENT_MAP.put("ps1", "#");
    }

    /** 根据 VirtualFile 推断注释符号，fallback 为 "#" */
    private String getCommentPrefix(@Nullable VirtualFile file) {
        if (file == null) return "#";

        // 无扩展名文件按文件名匹配（如 Dockerfile）
        String name = file.getName().toLowerCase();
        if (name.equals("dockerfile") || name.startsWith("dockerfile.")) return "#";
        if (name.equals("makefile")) return "#";

        String ext = file.getExtension();
        if (ext == null) return "#";
        return COMMENT_MAP.getOrDefault(ext.toLowerCase(), "#");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String prefix = getCommentPrefix(file);

        Project project = e.getProject();
        if (project == null) project = ProjectManager.getInstance().getDefaultProject();

        final Document document = editor.getDocument();
        final SelectionModel selection = editor.getSelectionModel();
        final Project finalProject = project;
        final String commentPrefix = prefix + " "; // 注释符后加一个空格

        WriteCommandAction.runWriteCommandAction(finalProject, () -> {
            int startLine, endLine;
            if (selection.hasSelection()) {
                startLine = document.getLineNumber(selection.getSelectionStart());
                endLine   = document.getLineNumber(selection.getSelectionEnd());
                if (selection.getSelectionEnd() == document.getLineStartOffset(endLine) && endLine > startLine) {
                    endLine--;
                }
            } else {
                startLine = document.getLineNumber(editor.getCaretModel().getOffset());
                endLine   = startLine;
            }

            // 所有行都已注释 → 取消注释，否则 → 添加注释
            boolean allCommented = true;
            for (int line = startLine; line <= endLine; line++) {
                String trimmed = getLineText(document, line).trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith(prefix)) {
                    allCommented = false;
                    break;
                }
            }

            for (int line = endLine; line >= startLine; line--) {
                if (allCommented) {
                    uncommentLine(document, line, prefix);
                } else {
                    commentLine(document, line, commentPrefix);
                }
            }
        });
    }

    private String getLineText(Document document, int line) {
        return document.getText().substring(
                document.getLineStartOffset(line),
                document.getLineEndOffset(line));
    }

    private void commentLine(Document document, int line, String commentPrefix) {
        String text = getLineText(document, line);
        if (text.trim().isEmpty()) return;
        int indent = countIndent(text);
        document.insertString(document.getLineStartOffset(line) + indent, commentPrefix);
    }

    private void uncommentLine(Document document, int line, String prefix) {
        String text = getLineText(document, line);
        if (text.trim().isEmpty()) return;
        int indent = countIndent(text);
        if (indent >= text.length() || !text.substring(indent).startsWith(prefix)) return;

        int absPos = document.getLineStartOffset(line) + indent;
        int removeLen = prefix.length();
        // 若注释符后跟了空格，一并删除
        if (indent + removeLen < text.length() && text.charAt(indent + removeLen) == ' ') {
            removeLen++;
        }
        document.deleteString(absPos, absPos + removeLen);
    }

    private int countIndent(String text) {
        int i = 0;
        while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '\t')) i++;
        return i;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        boolean isStandaloneDiff = (project == null);
        boolean writable = editor != null && editor.getDocument().isWritable();

        e.getPresentation().setEnabledAndVisible(isStandaloneDiff && writable);
    }
}