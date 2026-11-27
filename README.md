# COSMO To-Do System: UI (JavaFX, light theme)

The whole interface is finished and split into small files, one purpose each.
The functionality (tasks, saving, filtering ...) is yours. Everything on screen is **sample data**.

## 1. Run it
1. IntelliJ: **File > Open**, choose `pom.xml`, **Open as Project**, wait for Maven to finish.
2. Run **`cosmo.Launcher`** (not `Main`). Needs **JDK 24+** (JavaFX 26).
3. Harmless console warnings can be silenced with VM options:
   `--enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow`
4. `javafx.version` in `pom.xml` must be the same as (or newer than) your Scene Builder's JavaFX
   version, because Scene Builder writes it into every FXML (`xmlns="http://javafx.com/javafx/26"`).

## 2. Files: one FXML + one controller per screen part
```
resources/cosmo/fxml/               java (cosmo.ui...)
  App.fxml        shell, window size   MainController        wires parts together, implements TaskActions
  Sidebar.fxml    logo, page buttons   parts/SidebarController
  Dashboard.fxml  dashboard page       pages/DashboardController
  AllTasks.fxml   all tasks page       pages/AllTasksController
  ScheduleRow.fxml one dashboard row   parts/ScheduleRowController   (factory: create(...))
  TaskDetails.fxml side panel          parts/TaskDetailsController
  TaskForm.fxml   New/Edit popup       popups/TaskFormController
  ConfirmDelete.fxml delete popup      popups/ConfirmDeleteController
resources/cosmo/css/
  theme.css (colors only)  layout.css  controls.css  badges.css  table.css
Shared Java (cosmo.ui):
  TaskActions   the 5 things a screen can ask for (new, edit, delete, toggle done, change status)
  TaskView      one task, as the screen shows it       TaskInput  what the form collected
  TaskOptions   every dropdown choice, once            UiUtil     show/hide + CSS class helpers
  parts/TaskTableCells   draws the table cells         DemoData   DEMO ONLY, delete later
```

## 3. How the parts talk (read this once)
```
 Sidebar ──page chosen──▶ MainController ──shows/hides──▶ Dashboard / AllTasks pages
 Dashboard, AllTasks, rows, panel ──TaskActions──▶ MainController  (YOUR code runs here)
 MainController.refreshAll() ──setTasks / showStats / showSchedule──▶ the pages
```
A screen never changes a task itself. It calls a `TaskActions` method; `MainController` does the
work (your code), then calls `refreshAll()`, and the screens redraw.

## 4. Where do I change things?
| I want to change...                  | Go to |
|--------------------------------------|-------|
| a color                              | `theme.css` (one line) |
| window size                          | root node of `App.fxml` |
| a table column width                 | `AllTasks.fxml`, the `<TableColumn>` `prefWidth` / `minWidth` |
| dashboard row design / column widths | `ScheduleRow.fxml` / `layout.css` section 4 |
| table row height                     | `ROW_HEIGHT` in `AllTasksController` AND `-fx-cell-size` in `table.css` |
| dropdown choices                     | `TaskOptions.java` |
| the logo                             | `Sidebar.fxml` (replace the Label with an ImageView) |

Rule of thumb: **sizes in FXML, looks in CSS, behavior in Java.** No hex code outside `theme.css`.

## 5. Adding your functionality (search the project for TODO)
1. **`MainController.refreshAll()`**: replace the DEMO lines with your real data.
2. **`MainController`**: `saveTask`, `deleteTask`, `toggleCompleted`, `changeStatus`.
3. **`AllTasksController`**: filters + sort (FilteredList + SortedList, then `setTasks(...)`).
4. Delete `DemoData.java` when nothing uses it.

## 6. Designing in Scene Builder
* Open any `.fxml`. To see the real look: **Preview > Scene Style Sheets > Add**, pick the 5 css files.
* Hidden parts (`TaskDetails`, `TaskForm`, `ConfirmDelete` roots): select the root and tick **Visible**
  to design; **untick before saving**.
* `Dashboard.fxml` shows an empty schedule list on purpose. Design the row in `ScheduleRow.fxml`.

## 7. Design patterns and principles used (for your reflection)
| Idea | Where |
|------|-------|
| Single responsibility | one FXML + one controller per screen part; `TaskTableCells` only draws cells |
| Dependency inversion / interface segregation | pages depend on the small `TaskActions` interface, not on `MainController` |
| Observer (callbacks) | `SidebarController.setOnPageChange`, `TaskDetailsController.setOnClosed`, `TaskFormController` save callback |
| Factory method | `ScheduleRowController.create(...)` loads the FXML and returns a filled row |
| DRY | `TaskOptions` (dropdown choices), `UiUtil` (CSS helpers), `theme.css` (colors), `ScheduleRow.fxml` (row design) |
| KISS | no inline styles, one interface with 5 methods, screens only display |
Strategy (sorters/filters) and Singleton (database) belong in YOUR model code.
