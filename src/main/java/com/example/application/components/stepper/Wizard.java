package com.example.application.components.stepper;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.example.application.components.stepper.Step.HasBinder;
import com.example.application.components.stepper.Step.State;
import com.example.application.components.stepper.Stepper.Orientation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.IconSize;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public abstract class Wizard extends Main
		implements BeforeEnterObserver, BeforeLeaveObserver, RouterLayout, AfterNavigationObserver {

	private final Stepper stepper;
	private Div content;
	private Div footer;
	private RouterLink previous;
	private RouterLink next;
	private String currentPath;
	private Step[] steps;
	
	private ContinueNavigationAction action;
	private ConfirmDialog confirmDialog = new ConfirmDialog("",
			super.getTranslation("wizard.validation.errors"), super.getTranslation("OK"), __ -> action.cancel());

	public Wizard(Step... steps) {
		this.steps = steps;
		stepper = new Stepper(steps);
		addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);
		add(createStepper(), createContent(), createFooter());
	}

	public Wizard(Orientation orientiation, Step... steps) {
		this(steps);
		setOrientiation(orientiation);
	}
	
	public void setOrientiation(Orientation orientiation) {
		removeClassName(orientiation == Orientation.HORIZONTAL ? FlexDirection.ROW: FlexDirection.COLUMN);
		addClassName(orientiation == Orientation.HORIZONTAL ? FlexDirection.COLUMN: FlexDirection.ROW);
		stepper.setOrientation(orientiation);
	}
	
	private Stepper createStepper() {
		this.stepper.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.MEDIUM);
		this.stepper.setOrientation(Stepper.Orientation.HORIZONTAL);
		this.stepper.setSmall(true);
		return this.stepper;
	}

	private Div createContent() {
		this.content = new Div();
		this.content.addClassNames("flex-1", Overflow.AUTO);
		return this.content;
	}

	private Div createFooter() {
		new RouterLink();
		this.previous = new RouterLink();
		this.previous.setText("Previous");
		this.previous.addComponentAsFirst(createSmallIcon(LineAwesomeIcon.ANGLE_LEFT_SOLID));
		this.previous.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL);

		this.next = new RouterLink();
		this.next.setText("Next");
		this.next.add(createSmallIcon(LineAwesomeIcon.ANGLE_RIGHT_SOLID));
		this.next.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL);

		this.footer = new Div(this.previous, this.next);
		this.footer.addClassNames(Display.FLEX, Gap.XLARGE, Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
		return this.footer;
	}

	private Component createSmallIcon(LineAwesomeIcon icon) {
		SvgIcon i = icon.create();
		i.addClassNames(IconSize.SMALL);
		return i;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void showRouterLayoutContent(HasElement content) {
		if (content != null) {
			this.content.removeAll();
			if (content instanceof HasBinder) {
				Binder binder = ((HasBinder) content).getBinder();
				binder.setBean(restoreSessionObject(content.getClass(), binder.getBean()));
			}
			this.content.getElement().appendChild(content.getElement());
		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		Boolean forwarded = (Boolean) ComponentUtil.getData(event.getUI(), "forwarded");
		ComponentUtil.setData(event.getUI(), "forwarded", null);
		if (forwarded != null && forwarded) {
			return;
		}
		Component view = getCurrentComponentView();
		if (view != null && view instanceof HasBinder) {
			Binder<?> binder = ((HasBinder)view).getBinder();
			saveSessionObject(view.getClass(), binder.getBean());
			int idxActive = getActiveStepIndex();
			int idxNext = getStepIndex(event.getLocation().getPath());
			if ((idxNext - idxActive) < 0) {
				return;
			}
			if (!binder.validate().isOk()) {
				this.action = event.postpone();
				confirmDialog.open();
				steps[idxActive].setState(State.ERROR);
			}
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		int idxActive = getActiveStepIndex();
		int idxNext = getStepIndex(event.getLocation().getPath());
		int diff = idxNext - idxActive;
		Integer forwardTo = null;
		if (idxActive == -1 && idxNext > 0) {
			forwardTo = 0;
		} else if (diff > 1) {
			forwardTo = idxActive;
		}
		if (forwardTo != null) {
			ComponentUtil.setData(event.getUI(), "forwarded", true);
			event.forwardTo(steps[forwardTo].getRouteClass());
		}
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		this.currentPath = event.getLocation().getPath();
		int l = steps.length;
		for (int i = 0; i < l; i++) {
			Step s = steps[i];
			if (s.getHref().equals(currentPath)) {
				saveSessionObject(getClass(), i);
				if (i == 0) {
					this.previous.getElement().removeAttribute("href");
				} else {
					this.previous.setRoute(s.getPrevStep().getRouteClass());
				}
				if (i == l - 1) {
					this.next.getElement().removeAttribute("href");
				} else {
					this.next.setRoute(s.getNextStep().getRouteClass());
				}
			}
		}
	}

	private int getStepIndex(String path) {
		for (int i = 0; i < steps.length; i++) {
			Step s = steps[i];
			if (s.getHref().equals(path)) {
				return i;
			}
		}
		return 0;
	}

	private int getActiveStepIndex() {
		for (int i = 0; i < steps.length; i++) {
			Step s = steps[i];
			if (s.getState() == State.ACTIVE || s.getState() == State.ERROR) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T getCurrentComponentView() {
		return (T) UI.getCurrent().getCurrentView();
	}

	public static void saveSessionObject(Class<?> clazz, Object value) {
		VaadinService.getCurrentRequest().getWrappedSession().setAttribute(clazz.getName(), value);
	}

	@SuppressWarnings("unchecked")
	public static <T> T restoreSessionObject(Class<?> clazz, T defaultValue) {
		T value = (T) VaadinService.getCurrentRequest().getWrappedSession().getAttribute(clazz.getName());
		return value == null ? defaultValue : value;
	}

}
