package com.vaadin.flow.component.textfield;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Server-side component for the {@code vaadin-short-field} element.
 *
 * @author Vaadin Ltd.
 */
@Tag("vaadin-integer-field")
@HtmlImport("frontend://bower_components/vaadin-text-field/src/vaadin-integer-field.html")
@JsModule("@vaadin/vaadin-text-field/src/vaadin-integer-field.js")
public class ShortField extends AbstractNumberField<ShortField, Short> {

    private static final SerializableFunction<String, Short> PARSER = valueFormClient -> {
        if (valueFormClient == null || valueFormClient.isEmpty()) {
            return null;
        }
        try {
            return Short.parseShort(valueFormClient);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    private static final SerializableFunction<Short, String> FORMATTER = valueFromModel -> valueFromModel == null
            ? ""
            : valueFromModel.toString();

    /**
     * Constructs an empty {@code IntegerField}.
     */
    public ShortField() {
        super(PARSER, FORMATTER, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /**
     * Constructs an empty {@code IntegerField} with the given label.
     *
     * @param label
     *            the text to set as the label
     */
    public ShortField(String label) {
        this();
        setLabel(label);
    }

    /**
     * Constructs an empty {@code IntegerField} with the given label and
     * placeholder text.
     *
     * @param label
     *            the text to set as the label
     * @param placeholder
     *            the placeholder text to set
     */
    public ShortField(String label, String placeholder) {
        this(label);
        setPlaceholder(placeholder);
    }

    /**
     * Constructs an empty {@code IntegerField} with a value change listener.
     *
     * @param listener
     *            the value change listener
     *
     * @see #addValueChangeListener(ValueChangeListener)
     */
    public ShortField(
            ValueChangeListener<? super ComponentValueChangeEvent<ShortField, Short>> listener) {
        this();
        addValueChangeListener(listener);
    }

    /**
     * Constructs an empty {@code IntegerField} with a value change listener and
     * a label.
     *
     * @param label
     *            the text to set as the label
     * @param listener
     *            the value change listener
     *
     * @see #setLabel(String)
     * @see #addValueChangeListener(ValueChangeListener)
     */
    public ShortField(String label,
                      ValueChangeListener<? super ComponentValueChangeEvent<ShortField, Short>> listener) {
        this(label);
        addValueChangeListener(listener);
    }

    /**
     * Constructs a {@code IntegerField} with a value change listener, a label
     * and an initial value.
     *
     * @param label
     *            the text to set as the label
     * @param initialValue
     *            the initial value
     * @param listener
     *            the value change listener
     *
     * @see #setLabel(String)
     * @see #setValue(Object)
     * @see #addValueChangeListener(ValueChangeListener)
     */
    public ShortField(String label, Short initialValue,
                      ValueChangeListener<? super ComponentValueChangeEvent<ShortField, Short>> listener) {
        this(label);
        setValue(initialValue);
        addValueChangeListener(listener);
    }

    /**
     * Sets the minimum value of the field. Entering a value which is smaller
     * than {@code min} invalidates the field.
     * 
     * @param min
     *            the min value to set
     */
    public void setMin(short min) {
        super.setMin(min);
    }

    /**
     * Gets the minimum allowed value of the field.
     *
     * @return the min property of the field
     * @see #setMin(short)
     */
    public short getMin() {
        return (short) getMinDouble();
    }

    /**
     * Sets the maximum value of the field. Entering a value which is greater
     * than {@code max} invalidates the field.
     *
     * @param max
     *            the max value to set
     */
    public void setMax(short max) {
        super.setMax(max);
    }

    /**
     * Gets the maximum allowed value of the field.
     *
     * @return the max property of the field
     * @see #setMax(short)
     */
    public short getMax() {
        return (short) getMaxDouble();
    }

    /**
     * Sets the allowed number intervals of the field. This specifies how much
     * the value will be increased/decreased when clicking on the
     * {@link #setHasControls(boolean) control buttons}. It is also used to
     * invalidate the field, if the value doesn't align with the specified step
     * and {@link #setMin(short) min} (if specified by user).
     * 
     * @param step
     *            the new step to set
     * @throws IllegalArgumentException
     *             if the argument is less or equal to zero.
     */
    public void setStep(short step) {
        if (step <= 0) {
            throw new IllegalArgumentException("The step cannot be less or equal to zero.");
        }
        super.setStep(step);
    }

    /**
     * Gets the allowed number intervals of the field.
     *
     * @return the step property of the field
     * @see #setStep(short)
     */
    public short getStep() {
        return (short) getStepDouble();
    }

}
