package com.thomasvitale.demo.config;

import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;

@Push
@PWA(name = "Developer's Nightmare", shortName = "Developer's Nightmare")
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("styles.css")
@Configuration(proxyBeanMethods = false)
public final class VaadinConfig implements AppShellConfigurator {}
