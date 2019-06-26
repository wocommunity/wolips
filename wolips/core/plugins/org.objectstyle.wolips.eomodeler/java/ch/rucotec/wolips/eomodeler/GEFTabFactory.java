package ch.rucotec.wolips.eomodeler;

public class GEFTabFactory {
	
	private static IGEFDiagramTab diagramTab;
	
	public static IGEFDiagramTab getDiagramTab() {
		if (diagramTab == null) {
			Double javaVersion = Double.parseDouble(System.getProperty("java.version").substring(0, 3));
			if (javaVersion >= 11d) {
				diagramTab = new ErrorDisplayTab();
			} else {
				diagramTab = new DiagramTab();
			}
		}
		return diagramTab;
	}

}
