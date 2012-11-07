package gjavaapi.JTextAreaDocumentdocument

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJTextAreaDocumentdocument.htm
 
import java.awt._

import javax.swing._
import javax.swing.text._

class MainClass {
  def main(args:Array[String]) {
    var frame = new JFrame("Sharing Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var content = frame.getContentPane();
    var textarea1 = new JTextArea();
    var document = textarea1.getDocument();
    //var textarea2:JTextArea = new JTextArea(document) //r=3
    var textarea2:JTextArea =  /*!*/ //r=3
    var textarea3:JTextArea = new JTextArea(document)
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.add(new JScrollPane(textarea1));
    content.add(new JScrollPane(textarea2));
    content.add(new JScrollPane(textarea3));
    frame.setSize(300, 400);
    frame.setVisible(true);
  }
}


/*
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

public class MainClass {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Sharing Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container content = frame.getContentPane();
    JTextArea textarea1 = new JTextArea();
    Document document = textarea1.getDocument();
    JTextArea textarea2 = new JTextArea(document);
    JTextArea textarea3 = new JTextArea(document);
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.add(new JScrollPane(textarea1));
    content.add(new JScrollPane(textarea2));
    content.add(new JScrollPane(textarea3));
    frame.setSize(300, 400);
    frame.setVisible(true);
  }
}
*/
