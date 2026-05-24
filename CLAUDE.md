# Vaadin Showcase Application

## Project Overview

Feature-rich Vaadin 25.1 showcase application demonstrating modern Vaadin components, patterns, and integrations including Signals (reactive state management). Built with Spring Boot 4.0 and Java 21.

## Tech Stack

| Technology | Version | Notes |
|---|---|---|
| Vaadin | 25.1 | Flow (Java-based UI), NOT React/Hilla. Signals support. |
| Spring Boot | 4.0.4 | Parent POM (4.0.4+ required by Vaadin 25.1) |
| Java | 21 | Required minimum |
| H2 Database | runtime | In-memory, initialized from `data.sql` |
| Spring AI | 2.0.0-M1 | OpenAI integration (requires `OPENAI_API_KEY` env var) |
| Maven | - | Build tool, `mvn spring-boot:run` to start |

## Project Structure

```
src/main/java/com/example/application/
  Application.java              # Spring Boot entry point, @Push, @Theme("show-case")
  security/
    SecurityConfiguration.java  # Spring Security config (BCrypt, login view, API permits)
    UserDetailsServiceImpl.java # In-memory user authentication
  data/
    AbstractEntity.java         # Base JPA entity (id, version)
    SamplePerson.java           # Main entity (firstName, lastName, email, phone, dateOfBirth, occupation, role, important)
    SamplePersonRepository.java # JPA + Specifications repository
  services/
    SamplePersonService.java    # CRUD + pagination + filtering
    SamplePersonServiceRest.java # REST API service
  components/
    Stepper.java, Step.java     # Multi-step navigation
    Wizard.java                 # Router-based wizard layout
    TabbedLayout.java, TabDef.java # Reusable tabbed RouterLayout
    PhoneNumberField.java       # Custom composite field
    LocalDatePicker.java        # LocalDate wrapper for DatePicker
    ToggleButton.java           # Toggle switch component
    SamplePersonFilter.java     # JPA Specification-based filtering
  views/
    MainLayout.java             # AppLayout with SideNav drawer + logout
    (25+ views - see below)
```

## Key Views

- **HelloWorldView** - Simple greeting demo with reactive Signal binding
- **SignalsPlaygroundView** - `/signals` route with 16 demos covering every Signals API (ValueSignal, ListSignal, Signal.computed, Signal.effect, bindText/Visible/Enabled/Value/ReadOnly/ClassName/ThemeName/HelperText/Placeholder/Width/Style, bindChildren, windowSizeSignal, localeSignal, flashClass)
- **DashboardView** - Charts and metrics; year selector wired to a `ValueSignal<String>` + `Signal.effect` that rebuilds the chart series reactively
- **FeedView** - Social feed cards backed by a `ListSignal<Person>` and `Signal.effect(grid, () -> grid.setItems(...))` (canonical Vaadin docs pattern)
- **DataGridView** - GridPro with 4 filters bound via `ValueSignal`s, `Signal.computed` filtered list, and a single `Signal.effect` on `grid.setItems(...)`
- **MasterDetailView** / **MasterDetailResponsiveView** - Split layout CRUD (responsive view uses Signals for detail panel visibility)
- **GridEditView** / **GridwithFiltersView** / **GridEditPaginatedView** - Data grid variants (filter views use Signals for mobile filter toggle)
- **CheckoutFormView** - Checkout form with Signal driven conditional visibility (state select)
- **CheckoutWizard** (4 steps) - Router-based multi-step form; the Wizard tracks the current step in a `ValueSignal<Integer>` and derives previous/next visibility and routes from it
- **SampleTabbedLayout** (List, Form, Details) - Tabbed RouterLayout demo at `/tabbed`
- **ChatView** - Vaadin Collaboration Engine messaging; unread badge per channel uses `ValueSignal<Integer>`, mobile detection uses `Page.windowSizeSignal()`
- **AiChatView** - OpenAI chat with voice support; reactive input/response/streaming signals drive `bindEnabled` and `bindReadOnly` on the Ask button and reply area
- **SpreadsheetView** - Excel editing; invoice header visibility via `ValueSignal<Boolean>`
- **MapView** - Maps with markers; search filter implemented with `ValueSignal<String>` + computed list + two `Signal.effect`s rebuilding cards and markers
- **ImageGalleryView** - Unsplash image grid
- **CrudView**, **AddonsView**, **CreditCardFormView**, **AddressFormView**

## Security

- All views have `@PermitAll` (login required, any role)
- Login via `LoginView` at `/login`
- REST API at `/api/**` is open (no auth, CSRF ignored)
- Static resources (`/images/*.png`, `/line-awesome/**`) are public

## Database

- H2 in-memory database, auto-initialized
- `data.sql` seeds ~50 SamplePerson records
- JPA with Hibernate, sequence-based ID generation (starts at 1000)

## Addons (from Vaadin Directory)

- `vaadin-spreadsheet-flow` - Spreadsheet component
- `grid-pagination` (org.vaadin.klaudeta) - Grid pagination
- `voice-engine` (org.vaadin.addons.manolo) - Voice recognition
- `vcf-input-mask` (org.vaadin.addons.componentfactory) - Input masking
- `line-awesome` (org.parttio) - Icon library

## Frontend / Theme

- Theme: `show-case` (Lumo light variant)
- CSS at `src/main/frontend/themes/show-case/`
- View-specific CSS files in `themes/show-case/views/`
- Component styles in `themes/show-case/components/`
- Uses LumoUtility classes extensively

## Build & Run

```bash
# Development
mvn spring-boot:run

# Production build
mvn clean package -Pproduction

# Run production JAR
java -jar target/show-case-1.0-SNAPSHOT.jar
```

Server starts on port 8080 (configurable via `PORT` env var).

## Patterns & Conventions

- **All views** use `@Route`, `@Menu`, `@PageTitle`, `@PermitAll`
- **Data binding** with `BeanValidationBinder`
- **Signals** (`ValueSignal`, `ListSignal`, `Signal.computed`, `Signal.effect`) for reactive UI state across the showcase: `bindText`, `bindVisible`, `bindEnabled`, `bindValue` (two-way), `bindReadOnly`, `bindClassName(s)`, `bindThemeName(s)`, `bindHelperText`, `bindPlaceholder`, `bindAttribute`, `bindWidth/Height`, `bindChildren` (for ListSignal), `getStyle().bind`, `getElement().getThemeList().bind`. Built-in sources: `Page.windowSizeSignal()`, `UI.localeSignal()`. Used in: HelloWorldView, CheckoutFormView, MasterDetailResponsiveView, GridwithFilters* views, FeedView, DataGridView, ChatView, MapView, SpreadsheetView, MainLayout (dark mode + Aura toggle + reactive title), AiChatView, DashboardView (year selector), Stepper/Step/Wizard, and the dedicated SignalsPlaygroundView playground.
- **Filtering** via JPA Specifications (`SamplePersonFilter`)
- **Layouts** use `LumoUtility` CSS classes for spacing/alignment
- **Custom fields** extend `CustomField<T>`
- **Wizard** uses `RouterLayout` with session-scoped data in `VaadinSession`
- **TabbedLayout** uses `RouterLayout` with `Tabs` synced to child route URLs via regex matching
- **Real-time** features use `@Push` and Collaboration Engine

## Important Notes

- This is a **Vaadin Flow (Java)** project - UI is built entirely in Java, NOT React/Hilla
- When using Vaadin MCP tools, use `ui_language: "java"` and `vaadin_version: "25.1"`
- The `src/main/frontend/generated/` directory is auto-generated - do not edit manually
- The `src/main/bundles/` directory contains pre-built frontend bundles
