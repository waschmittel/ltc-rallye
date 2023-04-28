package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.Sponsor.Fields;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public class SponsorEditor {
    private final BigDecimal shekelToEuro;

    public SponsorEditor(Sponsor sponsor, BigDecimal shekelToEuro, Predicate<Sponsor> submitHandler) {
        this.shekelToEuro = shekelToEuro;
        new BeanEditDialog<>(sponsor, Sponsor.class, this::bindFields, submitHandler);
    }

    private Optional<List<Component>> bindFields(Binder<Sponsor> binder, Sponsor sponsor) {
        TextField name = new TextField(I18n.SPONSOR_NAME.get());
        binder.forField(name).bind(Fields.name);

        TextField perLapDonation = currencyTextField("€", I18n.SPONSOR_PERLAP.get());
        TextField oneTimeDonation = currencyTextField("€", I18n.SPONSOR_ONETIME.get());
        perLapDonation.addValueChangeListener(e -> binder.getBinding(Fields.oneTimeDonation).ifPresent(Binding::validate));
        oneTimeDonation.addValueChangeListener(e -> binder.getBinding(Fields.perLapDonation).ifPresent(Binding::validate));
        binder.forField(perLapDonation)
                .withValidator(value ->
                                isValid(oneTimeDonation, perLapDonation),
                        I18n.SPONSOR_NO_DONATION.get())
                .withConverter(new LocaleIndependentMoneyConverter())
                .bind(Fields.perLapDonation);
        binder.forField(oneTimeDonation)
                .withValidator(value ->
                                isValid(oneTimeDonation, perLapDonation),
                        I18n.SPONSOR_NO_DONATION.get())
                .withConverter(new LocaleIndependentMoneyConverter())
                .bind(Fields.oneTimeDonation);

        TextField perLapShekels = currencyTextField("₪", I18n.SPONSOR_PERLAP_SHEKEL.get());
        addShekelConversion(perLapShekels, perLapDonation);

        TextField oneTimeShekels = currencyTextField("₪", I18n.SPONSOR_ONETIME_SHEKEL.get());
        addShekelConversion(oneTimeShekels, oneTimeDonation);

        return Optional.of(List.of(name, perLapDonation, perLapShekels, oneTimeDonation, oneTimeShekels));
    }

    private static boolean isValid(TextField oneTimeDonation, TextField perLapDonation) {
        return !StringUtils.isBlank(perLapDonation.getValue()) || !StringUtils.isBlank(oneTimeDonation.getValue());
    }

    private static TextField currencyTextField(String currency, String label) {
        var prefix = new Div();
        prefix.setText(currency);
        var textField = new TextField(label);
        textField.setPrefixComponent(prefix);
        return textField;
    }

    private void addShekelConversion(TextField source, TextField target) {
        var converter = new LocaleIndependentMoneyConverter();
        source.setValueChangeMode(EAGER);
        source.addValueChangeListener(event -> {
            try {
                BigDecimal shekels = converter.parse(source.getValue());
                BigDecimal euros = shekels.multiply(shekelToEuro).setScale(2, RoundingMode.HALF_UP);
                target.setValue(euros.toPlainString());
                source.setErrorMessage(null);
            } catch (NumberFormatException | ParseException e) {
                source.setErrorMessage(I18n.SPONSOR_INVALID_NUMBER.get());
            }
        });
    }

    private static class LocaleIndependentMoneyConverter implements Converter<String, BigDecimal> {
        private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        LocaleIndependentMoneyConverter() {
            if (numberFormat instanceof DecimalFormat decimalFormat) {
                decimalFormat.setParseBigDecimal(true);
                decimalFormat.setMinimumFractionDigits(2);
                decimalFormat.setMaximumFractionDigits(2);
                decimalFormat.setGroupingUsed(false);
            }
        }

        @Override
        public Result<BigDecimal> convertToModel(String value, ValueContext context) {
            return "".equals(value) ? Result.ok(null) :
                    parseCurrency(value);
        }

        private Result<BigDecimal> parseCurrency(String value) {
            try {
                return Result.ok(parse(value));
            } catch (ParseException | NumberFormatException nfe) {
                return Result.error(I18n.SPONSOR_INVALID_NUMBER.get());
            }
        }

        private BigDecimal parse(String value) throws ParseException {
            return ((BigDecimal) numberFormat.parse(value.trim().replace(",", "."))).setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public String convertToPresentation(BigDecimal value, ValueContext context) {
            return value == null || BigDecimal.ZERO.equals(value) ? "" : numberFormat.format(value);
        }
    }
}
