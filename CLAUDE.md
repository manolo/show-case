# Vaadin Showcase Application

## Project Overview

Feature-rich Vaadin 24 showcase application demonstrating modern Vaadin components, patterns, and integrations. Built with Spring Boot 3.5 and Java 21.

## Tech Stack

| Technology | Version | Notes |
|---|---|---|
| Vaadin | 24.9.2 | Flow (Java-based UI), NOT React/Hilla |
| Spring Boot | 3.5.6 | Parent POM |
| Java | 21 | Required minimum |
| H2 Database | runtime | In-memory, initialized from `data.sql` |
| Spring AI | 1.0.3 | OpenAI integration (requires `OPENAI_API_KEY` env var) |
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
    PhoneNumberField.java       # Custom composite field
    LocalDatePicker.java        # LocalDate wrapper for DatePicker
    ToggleButton.java           # Toggle switch component
    SamplePersonFilter.java     # JPA Specification-based filtering
  views/
    MainLayout.java             # AppLayout with SideNav drawer + logout
    (25+ views - see below)
```

## Key Views

- **HelloWorldView** - Simple greeting demo
- **DashboardView** - Charts and metrics
- **FeedView** - Social feed cards
- **MasterDetailView** / **MasterDetailResponsiveView** - Split layout CRUD
- **GridEditView** / **GridwithFiltersView** / **GridEditPaginatedView** - Data grid variants
- **CheckoutWizard** (4 steps) - Router-based multi-step form
- **ChatView** - Vaadin Collaboration Engine messaging
- **AiChatView** - OpenAI chat with voice support (Spring AI + VoiceEngine addon)
- **SpreadsheetView** - Excel editing (vaadin-spreadsheet-flow)
- **MapView** - Leaflet maps with markers
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
- **Filtering** via JPA Specifications (`SamplePersonFilter`)
- **Layouts** use `LumoUtility` CSS classes for spacing/alignment
- **Custom fields** extend `CustomField<T>`
- **Wizard** uses `RouterLayout` with session-scoped data in `VaadinSession`
- **Real-time** features use `@Push` and Collaboration Engine

## Important Notes

- This is a **Vaadin Flow (Java)** project - UI is built entirely in Java, NOT React/Hilla
- When using Vaadin MCP tools, use `ui_language: "java"` and `vaadin_version: "24"`
- The `src/main/frontend/generated/` directory is auto-generated - do not edit manually
- The `src/main/bundles/` directory contains pre-built frontend bundles
