package com.vaadin.tutorial.addressbook;

import com.vaadin.event.ShortcutAction;
import com.vaadin.tutorial.addressbook.backend.Task;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.TextArea;

/* Create custom UI Components.
 *
 * Create your own Vaadin components by inheritance and composition.
 * This is a form component inherited from VerticalLayout. Use
 * Use BeanFieldGroup to bind data fields from DTO to UI fields.
 * Similarly named field by naming convention or customized
 * with @PropertyId annotation.
 */
public class TaskForm extends FormLayout {

	Button save = new Button("Save", this::save);
	Button cancel = new Button("Cancel", this::cancel);
	Button delete = new Button("Delete", this::delete);
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextArea task = new TextArea("Task");
	DateField startDate = new DateField("Start date");
	DateField expectedEndDate = new DateField("Expected end date");

	Task currentTask;

	// Easily bind forms to beans and manage validation and buffering
	BeanFieldGroup<Task> formFieldBindings;

	public TaskForm() {
		configureComponents();
		buildLayout();
	}

	private void configureComponents() {
		/*
		 * Highlight primary actions.
		 *
		 * With Vaadin built-in styles you can highlight the primary save button
		 * and give it a keyboard shortcut for a better UX.
		 */
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		setVisible(false);
	}

	private void buildLayout() {
		setSizeUndefined();
		setMargin(true);

		HorizontalLayout actions = new HorizontalLayout(save, cancel);
		actions.setSpacing(true);

		addComponents(firstName, lastName, task,
				startDate, expectedEndDate, actions, delete);
	}

	/*
	 * Use any JVM language.
	 *
	 * Vaadin supports all languages supported by Java Virtual Machine 1.6+.
	 * This allows you to program user interface in Java 8, Scala, Groovy or any
	 * other language you choose. The new languages give you very powerful tools
	 * for organizing your code as you choose. For example, you can implement
	 * the listener methods in your compositions or in separate controller
	 * classes and receive to various Vaadin component events, like button
	 * clicks. Or keep it simple and compact with Lambda expressions.
	 */
	public void save(Button.ClickEvent event) {
		try {
			// Commit the fields from UI to DAO
			formFieldBindings.commit();

			// Save DAO to backend with direct synchronous service API
			getUI().service.save(currentTask);

			String msg = String.format("Saved '%s'.", currentTask.getTask());
			Notification.show(msg, Type.TRAY_NOTIFICATION);
			getUI().refreshTasks();
		} catch (FieldGroup.CommitException e) {
			// Validation exceptions could be shown here
		}
	}

	public void cancel(Button.ClickEvent event) {
		// Place to call business logic.
		Notification.show("Cancelled", Type.TRAY_NOTIFICATION);
		getUI().taskList.select(null);
		setVisible(false);
	}

	public void delete(Button.ClickEvent event) {
		Notification.show("Task Deleted", Type.TRAY_NOTIFICATION);
		getUI().service.delete(currentTask);
		getUI().taskList.select(null);
		getUI().refreshTasks();
	}

	void edit(Task currentTask) {
		this.currentTask = currentTask;
		if (currentTask != null) {
			// Bind the properties of the task POJO to fiels in this form
			formFieldBindings = BeanFieldGroup.bindFieldsBuffered(currentTask,
					this);
			firstName.focus();
		}
		setVisible(currentTask != null);
	}

	@Override
	public TodoListUI getUI() {
		return (TodoListUI) super.getUI();
	}

}
