package hospital.model;

/**
 * OOP principle - POLYMORPHISM: Patient and Doctor both implement this
 * interface but produce very different report text. Code that only
 * knows about "Reportable" (e.g. ReportPanel) can treat both types
 * uniformly and calling toReportString() invokes the correct
 * overridden version at runtime.
 */
public interface Reportable {
    String toReportString();
}
