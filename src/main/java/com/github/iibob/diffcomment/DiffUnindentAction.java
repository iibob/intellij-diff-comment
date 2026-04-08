package com.github.iibob.diffcomment;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

public class DiffUnindentAction extends DumbAwareAction {

    private static final int INDENT_SIZE = 4; // 与 DiffIndentAction 保持一致

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        Project project = e.getProject();
        if (project == null) project = ProjectManager.getInstance().getDefaultProject();

        final Document document = editor.getDocument();
        final SelectionModel selection = editor.getSelectionModel();
        final Project finalProject = project;

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

            // 取消缩进需要从后往前处理，避免偏移量错乱
            for (int line = endLine; line >= startLine; line--) {
                unindentLine(document, line);
            }
        });
    }

    /**
     * 移除行首最多 INDENT_SIZE 个空格，或者 1 个 Tab。
     * 混合缩进时优先处理 Tab。
     */
    private void unindentLine(Document document, int line) {
        String text = getLineText(document, line);
        if (text.trim().isEmpty()) return;

        int lineStart = document.getLineStartOffset(line);

        if (text.charAt(0) == '\t') {
            // 行首是 Tab，删除一个 Tab
            document.deleteString(lineStart, lineStart + 1);
        } else {
            // 行首是空格，删除最多 INDENT_SIZE 个
            int spaces = 0;
            while (spaces < text.length()
                    && spaces < INDENT_SIZE
                    && text.charAt(spaces) == ' ') {
                spaces++;
            }
            if (spaces > 0) {
                document.deleteString(lineStart, lineStart + spaces);
            }
        }
    }

    private String getLineText(Document document, int line) {
        return document.getText().substring(
                document.getLineStartOffset(line),
                document.getLineEndOffset(line));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean writable = editor != null && editor.getDocument().isWritable();
        boolean enable = (e.getProject() == null) && writable;

//        e.getPresentation().setEnabledAndVisible(writable);  // 测试
        e.getPresentation().setEnabledAndVisible(enable);   // 正式
    }
}