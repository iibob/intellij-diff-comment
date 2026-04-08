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

public class DiffIndentAction extends DumbAwareAction {

    private static final String INDENT = "    "; // 4个空格，按需改成 "\t"

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
                // 选区末尾恰好在行首时，不包含该行
                if (selection.getSelectionEnd() == document.getLineStartOffset(endLine) && endLine > startLine) {
                    endLine--;
                }
            } else {
                startLine = document.getLineNumber(editor.getCaretModel().getOffset());
                endLine   = startLine;
            }

            // 从前往后插入，偏移量会自动更新
            for (int line = startLine; line <= endLine; line++) {
                String text = getLineText(document, line);
                if (!text.trim().isEmpty()) { // 空行不缩进
                    document.insertString(document.getLineStartOffset(line), INDENT);
                }
            }
        });
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