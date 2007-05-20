package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.WodFileDocumentProvider;

public class TemplateRefactoring {
  public static void processHtmlAndWod(IRunnableWithProgress runnable, WodParserCache cache, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    IDocument htmlDocument = cache.getHtmlDocument();
    FileEditorInput htmlInput = null;
    IDocumentProvider htmlProvider = null;
    if (htmlDocument == null) {
      IFile htmlFile = cache.getHtmlFile();
      if (htmlFile != null) {
        htmlInput = new FileEditorInput(cache.getHtmlFile());
        htmlProvider = new TextFileDocumentProvider();
        htmlProvider.connect(htmlInput);
        htmlDocument = htmlProvider.getDocument(htmlInput);
        cache.setHtmlDocument(htmlDocument);
      }
    }
    try {
      IDocument wodDocument = cache.getWodDocument();
      IDocumentProvider wodProvider = null;
      FileEditorInput wodInput = null;
      if (wodDocument == null) {
        IFile wodFile = cache.getWodFile();
        if (wodFile != null) {
          wodInput = new FileEditorInput(cache.getWodFile());
          wodProvider = new WodFileDocumentProvider();
          wodProvider.connect(wodInput);
          wodDocument = wodProvider.getDocument(wodInput);
          cache.setWodDocument(wodDocument);
        }
      }
      try {
        runnable.run(progressMonitor);
      }
      finally {
        if (wodProvider != null) {
          wodProvider.saveDocument(progressMonitor, wodInput, wodDocument, true);
          wodProvider.disconnect(wodInput);
          cache.setWodDocument(null);
        }
      }
    }
    finally {
      if (htmlProvider != null) {
        htmlProvider.saveDocument(progressMonitor, htmlInput, htmlDocument, true);
        htmlProvider.disconnect(htmlInput);
        cache.setHtmlDocument(null);
      }
    }
  }
}
