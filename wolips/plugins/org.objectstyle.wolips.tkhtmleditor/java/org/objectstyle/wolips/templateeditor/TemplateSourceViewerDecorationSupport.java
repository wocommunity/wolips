package org.objectstyle.wolips.templateeditor;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class TemplateSourceViewerDecorationSupport extends SourceViewerDecorationSupport {
  private static IDrawingStrategy _roundBoxStrategy = new RoundBoxDrawingStrategy();

  public TemplateSourceViewerDecorationSupport(ISourceViewer sourceViewer, IOverviewRuler overviewRuler, IAnnotationAccess annotationAccess, ISharedTextColors sharedTextColors) {
    super(sourceViewer, overviewRuler, annotationAccess, sharedTextColors);
  }

  @Override
  protected AnnotationPainter createAnnotationPainter() {
    AnnotationPainter annotationPainter = super.createAnnotationPainter();
    //annotationPainter.addDrawingStrategy(AnnotationPreference.STYLE_BOX, _roundBoxStrategy);
    return annotationPainter;
  }

  public static class RoundBoxDrawingStrategy implements IDrawingStrategy {
    /*
     * @see org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy#draw(org.eclipse.jface.text.source.Annotation, org.eclipse.swt.graphics.GC, org.eclipse.swt.custom.StyledText, int, int, org.eclipse.swt.graphics.Color)
     */
    public void draw(Annotation annotation, GC gc, StyledText textWidget, int offset, int length, Color color) {
      System.out.println("DragOverDrawingStrategy.draw: " + offset + ", " + length);
      if (length == 0) {
        return;
      }

      if (gc != null) {
        Rectangle bounds;
        if (length > 0) {
          bounds = textWidget.getTextBounds(offset, offset + length - 1);
        }
        else {
          Point loc = textWidget.getLocationAtOffset(offset);
          bounds = new Rectangle(loc.x, loc.y, 1, textWidget.getLineHeight(offset));
        }
        drawDragOver(gc, textWidget, color, bounds);
      }
      else {
        textWidget.redrawRange(offset, length, true);
      }
    }

    protected void drawDragOver(GC gc, StyledText textWidget, Color color, Rectangle bounds) {
      gc.setForeground(color);
      gc.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
    }
  }
}
