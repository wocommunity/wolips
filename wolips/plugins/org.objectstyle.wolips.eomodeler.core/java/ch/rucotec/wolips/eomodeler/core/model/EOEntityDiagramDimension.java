package ch.rucotec.wolips.eomodeler.core.model;

/**
 * This class is used to save the coordinates + the height and the width of an
 * EOEntityDiagram.
 * 
 * @author Savas Celik
 *
 */
public class EOEntityDiagramDimension {
	
	//---------------------------------------------------------------------------
	// ### Variables and Constants
	//---------------------------------------------------------------------------
	
	private Double xPos;
	private Double yPos;
	private Double width;
	private Double height;
	
	//---------------------------------------------------------------------------
	// ### Construction
	//---------------------------------------------------------------------------

	public EOEntityDiagramDimension(double xPos, double yPos, double width, double height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	//---------------------------------------------------------------------------
	// ### Basic Accessors
	//---------------------------------------------------------------------------

	public Double getxPos() {
		return xPos;
	}

	public void setxPos(Double xPos) {
		this.xPos = xPos;
	}

	public Double getyPos() {
		return yPos;
	}

	public void setyPos(Double yPos) {
		this.yPos = yPos;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}
}
