package tk.eclipse.plugin.htmleditor.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * ImageViewer.
 */
public class ImageView extends ViewPart implements ISelectionListener {
	
	private ArrayList<Image> imageList = new ArrayList<Image>();
	private ArrayList<Image> iconList  = new ArrayList<Image>();
	private SashForm sash;
	private Table table;
	private ScaleableImageCanvas canvas;
	
	public ImageView(){
		super();
	}
	
	@Override
  public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getPage().addSelectionListener(this);
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateView(selection);
	}
	
	@Override
  public void dispose(){
		getSite().getPage().removeSelectionListener(this);
		sash.dispose();
		super.dispose();
	}
	
	private void updateView(ISelection selection){
		table.removeAll();
		canvas.setImage(null);
		for(int i=0;i<imageList.size();i++){
			imageList.get(i).dispose();
			iconList.get(i).dispose();
		}
		imageList.clear();
		iconList.clear();
		
		if(selection instanceof IStructuredSelection){
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			if(obj instanceof IContainer){
				showContainer((IContainer)obj);
			} else if(obj instanceof IFile){
				showContainer(((IFile)obj).getParent());
			} else if(obj instanceof IJavaElement){
				IResource resource = ((IJavaElement)obj).getResource();
				if(resource instanceof IContainer){
					showContainer((IContainer)resource);
				} else if(resource instanceof IFile){
					showContainer(((IFile)resource).getParent());
				}
			}
		}
	}
	
	private void showContainer(IContainer folder){
		try {
			IResource[] resources = folder.members();
			for(int i=0;i<resources.length;i++){
				if(resources[i] instanceof IFile){
					IFile file = (IFile)resources[i];
					for(int j=0;j<HTMLPlugin.SUPPORTED_IMAGE_TYPES.length;j++){
						if(file.getFileExtension().equalsIgnoreCase(HTMLPlugin.SUPPORTED_IMAGE_TYPES[j])){
							addImage(file);
							break;
						}
					}
				}
			}
		} catch(Exception ex){
		}
	}
	
	private void addImage(IFile file){
		try {
			ImageData data = new ImageData(file.getLocation().makeAbsolute().toFile().getAbsolutePath());
			Image image = new Image(Display.getDefault(),data);
			
			Image i = new Image(Display.getDefault(), 32, 32);
			GC gc = new GC(i);
			gc.drawImage(
					image,0,0,
					image.getImageData().width,
					image.getImageData().height,
					0,0,32,32);
			gc.dispose();
			
			TableItem item = new TableItem(table,SWT.NULL);
			item.setImage(i);
			item.setText(file.getName() + " (" + image.getImageData().width + " * " + image.getImageData().height + ")");
			imageList.add(image);
			iconList.add(i);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
  public void createPartControl(Composite parent) {
		sash  = new SashForm(parent,SWT.VERTICAL);
		table = new Table(sash,SWT.BORDER);
		table.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				int index = table.getSelectionIndex();
				if(index >= 0){
					canvas.setImage(imageList.get(index));
				}
			}
		});
		Composite composite = new Composite(sash,SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight      = 0;
		layout.marginWidth       = 0;
		composite.setLayout(layout);
		CLabel label = new CLabel(composite,SWT.NULL);
		label.setText(HTMLPlugin.getResourceString("ImageView.Preview"));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		canvas = new ScaleableImageCanvas(composite,SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
  public void setFocus() {
		table.setFocus();
	}
	
	private class ScaleableImageCanvas extends Canvas {
		
		private Image image;
		private int width;
		private int height;
		private int ix = 0;
		private int iy = 0;
		
		public ScaleableImageCanvas(Composite parent, int style) {
			super(parent, style);
			setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					if(image==null){
						e.gc.fillRectangle(0,0,getSize().x,getSize().y);
						return;
					}
					int scaledWidth  = getSize().x;
					int scaledHeight = getSize().y;
					
					if(width <= scaledWidth && height <= scaledHeight){
						scaledWidth  = width;
						scaledHeight = height;
					} else {
						double scale = (double) scaledWidth / width;
						if(height * scale <= scaledHeight){
							scaledHeight = (int)(height * scale);
						} else {
							scale = (double) scaledHeight / height;
							scaledWidth  = (int)(width  * scale);
						}
					}
					e.gc.drawImage(
							ScaleableImageCanvas.this.image,
							0, 0, width, height, ix, iy, scaledWidth, scaledHeight);
				}
			});
		}
		
		public void setImage(Image image){
			this.image  = image;
			if(image!=null){
				this.width  = image.getImageData().width;
				this.height = image.getImageData().height;
			}
			redraw();
		}
		
		@Override
    public void dispose() {
			super.dispose();
		}
		
	}
}
