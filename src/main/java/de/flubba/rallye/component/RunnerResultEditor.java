package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Runner.Fields;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class RunnerResultEditor {

    private RunnerResultEditor() {
    }

    public static void edit(Runner runner, Predicate<Runner> submitHandler) {
        new BeanEditDialog<>(runner, Runner.class, RunnerResultEditor::bindFields, submitHandler);
    }

    private static Optional<List<Component>> bindFields(Binder<Runner> binder, Runner runner) {
        var id = new TextField(I18n.RUNNER_ID.get());
        id.setEnabled(false);
        id.setVisible(runner.getId() != null);
        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToLongConverter(""))
                .bind(Fields.id);

        var name = new TextField(I18n.RUNNER_NAME.get());
        name.setEnabled(false);
        binder.forField(name).bind(Fields.name);

        var bonusLaps = new NumberField(I18n.RESULTS_BONUS_LAPS.get());
        binder.forField(bonusLaps)
                .withConverter(new Converter<Double, Long>() {
                    @Override
                    public Result<Long> convertToModel(Double value, ValueContext context) {
                        return Result.ok(value.longValue());
                    }

                    @Override
                    public Double convertToPresentation(Long value, ValueContext context) {
                        return value == null ? 0D : value.doubleValue();
                    }
                })
                .bind(Fields.bonusLaps);

        return Optional.empty();
    }
}
