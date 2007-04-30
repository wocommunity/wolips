package tk.eclipse.plugin.jspeditor.editors;

public interface IJSPValidationMarkerCreator {

	public void addMarker( int severity, int offset, int length, String message);
}
