/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author wakan
 */
public class FindAndReplaceInDocX {

    public static String _nrPkd;
    public static String _sprawdzajacy;

    public void FindAndReplaceInDocX() throws IOException,
            InvalidFormatException,
            org.apache.poi.openxml4j.exceptions.InvalidFormatException {

        try {
            try (
                    XWPFDocument doc = new XWPFDocument(
                            OPCPackage.open("./data/wzor.docx"))) {
                
                for (XWPFParagraph p : doc.getParagraphs()) {
                    List<XWPFRun> runs = p.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            String text = r.getText(0);
                            System.out.println("Texty: " + text);
                            if (text != null && text.contains("_pkd_nr_")) {
                                text = text.replace("_pkd_nr_", "" + _nrPkd + "");
                                r.setText(text, 0);
                            }
                        }
                    }
                }

                doc.write(new FileOutputStream("./pkd/PKD " + _nrPkd + ".docx"));
                doc.close();
            }
        } finally {

        }
    }
}
