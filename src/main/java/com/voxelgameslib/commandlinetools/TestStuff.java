package com.voxelgameslib.commandlinetools;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class TestStuff extends DefaultHandler {

    private String tempVal;


    public TestStuff() {
        try {
            parseDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseDocument() throws Exception {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        //get a new instance of parser
        SAXParser sp = spf.newSAXParser();

        //parse the file and also register this class for call backs
        sp.parse("C:\\Users\\Martin\\IdeaProjects\\VoxelGamesLib2\\.idea\\workspace.xml", this);
    }

//    @Override
//    public void startElement(String uri, String localName, String qName,
//                             Attributes attributes) throws SAXException {
//        //reset
//        tempVal = "";
//        if (qName.equalsIgnoreCase("Employee")) {
//            //create a new instance of employee
//            tempEmp = new Employee();
//            tempEmp.setType(attributes.getValue("type"));
//        }
//    }
//
//
//    @Override
//    public void characters(char[] ch, int start, int length) throws SAXException {
//        tempVal = new String(ch, start, length);
//    }
//
//    @Override
//    public void endElement(String uri, String localName,
//                           String qName) throws SAXException {
//        if (qName.equalsIgnoreCase("Employee")) {
//            //add it to the list
//            myEmpls.add(tempEmp);
//
//        } else if (qName.equalsIgnoreCase("Name")) {
//            tempEmp.setName(tempVal);
//        } else if (qName.equalsIgnoreCase("Id")) {
//            tempEmp.setId(Integer.parseInt(tempVal));
//        } else if (qName.equalsIgnoreCase("Age")) {
//            tempEmp.setAge(Integer.parseInt(tempVal));
//        }
//
//    }
//
//    private void printData() {
//
//        System.out.println("No of Employees '" + myEmpls.size() + "'.");
//
//        Iterator it = myEmpls.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
//    }
}
