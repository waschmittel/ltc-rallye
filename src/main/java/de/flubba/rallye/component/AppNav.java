package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
public class AppNav extends Component implements HasSize, HasStyle {
    public AppNav(AppNavItem... appNavItems) {
        for (AppNavItem appNavItem : appNavItems) {
            getElement().appendChild(appNavItem.getElement());
        }
    }
}
