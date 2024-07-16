package com.example.application.components.datepicker;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import com.vaadin.flow.component.datepicker.DatePicker;

public class LocalDatePicker extends DatePicker {
	
	public LocalDatePicker(String label) {
		this();
		setLabel(label);
	}

	public LocalDatePicker() {
		// Use Default Locale in vaadin which looks for files like
		// src/main/resources/vaadin-i18n/translations_es_ES.properties
		// Note that if fi_FI does not exist uses the first one in resources folder.
		setLocale(getLocale());
	}
	
	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		DatePickerI18n i18n = new DatePickerI18n();

		DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
		i18n.setMonthNames(List.of(dfs.getMonths()));
		
		// DateFormatSymbols returns a list of 9 elements,
		// the first is empty, then Sun to Mon, but DatePicker
		// needs elements from Sun to Mon without the empty one
		i18n.setWeekdays(List.of(dfs.getWeekdays()).subList(1, 8));
		i18n.setWeekdaysShort(List.of(dfs.getShortWeekdays()).subList(1, 8));

		// WeekFields returns 1 for Sun, 8 for Mon,
		// but DatePicker needs 0 for Sun, 7 for Mon
		int firstDay = WeekFields.of(locale).getFirstDayOfWeek().getValue();
		i18n.setFirstDayOfWeek(firstDay % 7);
		// Lets adjust formats to use zeros and full year number
		String pattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale)).toPattern();
		String dateFormatSlash = pattern.replaceFirst("d+", "dd")
				.replaceFirst("M+", "MM")
				.replaceFirst("y+", "yyyy");
		String dateFormatDash = dateFormatSlash.replace("/", "-");
		// Add a list of valid formats
		i18n.setDateFormats(dateFormatDash, dateFormatSlash, pattern);
		
		// User needs to provide these in files like:
		// src/main/resources/vaadin-i18n/translations_en_US.properties
		i18n.setCancel(getTranslation("Cancel"));
		i18n.setToday(getTranslation("Today"));
		
		setI18n(i18n);
		setPlaceholder(dateFormatDash);
	}
	
	private String getTranslation(String key) {
		String ret = super.getTranslation(key);
		return ret.startsWith("!") ? key : ret;
	}
}
