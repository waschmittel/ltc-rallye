package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Sponsor;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.form.AbstractForm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public class SponsorEditForm extends AbstractForm<Sponsor> {
    private final BigDecimal shekelToEuro;

    private final TextField name = new TextField(I18n.SPONSOR_NAME.get());
    private final TextField street = new TextField(I18n.SPONSOR_STREET.get());
    private final TextField city = new TextField(I18n.SPONSOR_CITY.get());
    private final TextField country = new TextField(I18n.SPONSOR_COUNTRY.get());
    private final TextField perLapDonation = currencyTextField("€", I18n.SPONSOR_PERLAP.get());
    private final TextField perLapShekels = currencyTextField("₪", I18n.SPONSOR_PERLAP_SHEKEL.get());
    private final TextField oneTimeDonation = currencyTextField("€", I18n.SPONSOR_ONETIME.get());
    private final TextField oneTimeShekels = currencyTextField("₪", I18n.SPONSOR_ONETIME_SHEKEL.get());

    public SponsorEditForm(Sponsor sponsor, BigDecimal shekelToEuro) {
        super(Sponsor.class);
        this.shekelToEuro = shekelToEuro;

        setSaveCaption(I18n.SPONSOR_FORM_BUTTON_SAVE.get());
        setCancelCaption(I18n.SPONSOR_FORM_BUTTON_CANCEL.get());
        getDeleteButton().setVisible(false);

        addShekelConversion(oneTimeShekels, oneTimeDonation);
        addShekelConversion(perLapShekels, perLapDonation);

        setEntity(sponsor);
        if (sponsor.getId() != null) {
            getSaveButton().setEnabled(true);
        }
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

    @Override
    protected void bind() {
        getBinder().forField(oneTimeDonation)
                .withConverter(new LocaleIndependentMoneyConverter())
                .bind("oneTimeDonation");
        getBinder().forField(perLapDonation)
                .withConverter(new LocaleIndependentMoneyConverter())
                .bind("perLapDonation");
        super.bind();

        super.getBinder().withValidator((Validator<Sponsor>) (value, context) -> {
            if (StringUtils.isNoneEmpty(
                    value.getName(),
                    value.getCity(),
                    value.getCountry(),
                    value.getStreet()
            ) && value.getPerLapDonation() == null && value.getOneTimeDonation() == null) {
                perLapDonation.setInvalid(true);
                perLapDonation.setErrorMessage(I18n.SPONSOR_NO_DONATION.get());
                oneTimeDonation.setInvalid(true);
                oneTimeDonation.setErrorMessage(I18n.SPONSOR_NO_DONATION.get());
                return ValidationResult.error(I18n.SPONSOR_NO_DONATION.get());
            } else {
                perLapDonation.setInvalid(false);
                oneTimeDonation.setInvalid(false);
                return ValidationResult.ok();
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

    @Override
    protected Component createContent() {
        return new VerticalLayout(new FormLayout(name,
                street,
                city,
                country,
                perLapDonation,
                perLapShekels,
                oneTimeDonation,
                oneTimeShekels),
                getToolbar());
    }
}
