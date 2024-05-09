package com.devoxx.genie.ui;

import com.devoxx.genie.model.request.ChatMessageContext;
import com.devoxx.genie.ui.util.HelpUtil;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class PromptOutputPanel extends JBPanel<PromptOutputPanel> {

    private final JPanel container = new JPanel();
    private final WelcomePanel welcomePanel;
    private final HelpPanel helpPanel;
    private final WaitingPanel waitingPanel = new WaitingPanel();
    private final JBScrollPane scrollPane;

    /**
     * The prompt output panel.
     * @param resourceBundle the resource bundle
     */
    public PromptOutputPanel(ResourceBundle resourceBundle) {
        super();

        welcomePanel = new WelcomePanel(resourceBundle);
        helpPanel = new HelpPanel(HelpUtil.getHelpMessage(resourceBundle));

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        scrollPane = new JBScrollPane(container);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        showWelcomeText();
    }

    /**
     * Clear the panel and show welcome text.
     */
    public void clear() {
        container.removeAll();
        showWelcomeText();
        moveToBottom();
    }

    /**
     * Show the welcome text.
     */
    public void showWelcomeText() {
        welcomePanel.showMsg();
        container.add(welcomePanel);
    }

    /**
     * Show the help text.
     */
    public void showHelpText() {
        addFiller("help");
        container.add(helpPanel);
        moveToBottom();
    }

    /**
     * Add a filler to the panel.
     */
    private void addFiller(String name) {
        container.add(new FillerPanel(name));
    }

    /**
     * Add a user prompt to the panel.
     * @param chatMessageContext the prompt context
     */
    public void addUserPrompt(ChatMessageContext chatMessageContext) {
        container.remove(welcomePanel);
        waitingPanel.showMsg();

        UserPromptPanel userPromptPanel = new UserPromptPanel(container, chatMessageContext);
        userPromptPanel.add(waitingPanel, BorderLayout.SOUTH);
        addFiller(chatMessageContext.getName());
        container.add(userPromptPanel);
        moveToBottom();
    }

    /**
     * Add a response to the panel.
     *
     * @param chatMessageContext  the prompt context
     */
    public void addChatResponse(ChatMessageContext chatMessageContext) {
        waitingPanel.hideMsg();

        addFiller(chatMessageContext.getName());

        ChatResponsePanel chatResponsePanel = new ChatResponsePanel(chatMessageContext);
        chatResponsePanel.setName(chatMessageContext.getName());

        container.add(chatResponsePanel);

        moveToBottom();
    }

    /**
     * Add a warning text to the panel.
     *
     * @param chatMessageContext the prompt context
     * @param text          the warning text
     */
    public void addWarningText(ChatMessageContext chatMessageContext, String text) {
        welcomePanel.setVisible(false);
        addFiller("warning");
        container.add(new WarningPanel(text, chatMessageContext, text));
    }

    /**
     * Scroll to the bottom of the panel after repainting the new content.
     * SwingUtilities.invokeLater will schedule the scrolling to happen after all pending events are processed,
     */
    private void moveToBottom() {

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            // Ensure the viewport's contents are updated before fetching the maximum scroll value.
            // scrollPane.getViewport().validate();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
