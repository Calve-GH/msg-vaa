package com.bitlevex.messagehandler.view;

import com.bitlevex.messagehandler.dto.MessageDto;
import com.bitlevex.messagehandler.service.MessageService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@Route
public class MainView extends VerticalLayout {
    private final MessageService messageService;

    private Grid<MessageDto> grid = new Grid<>(MessageDto.class, false);

    @Autowired
    public MainView(MessageService messageService) {
        this.messageService = messageService;
        Label title = new Label("Message list");
        grid.addColumn("id")
                .setResizable(false)
                .setWidth("10px");
        grid.addColumn("date")
                .setWidth("20px");
        grid.addColumn("message")
                .setResizable(true)
                .setWidth("600px");
        grid.addColumn("ip")
                .setWidth("50px");
        grid.setColumnReorderingAllowed(true);
        grid.setHeightByRows(true);
        add(title);
        add(grid);
        grid.setItems(messageService.findAll());
    }
}
