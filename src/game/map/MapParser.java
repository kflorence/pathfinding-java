package game.map;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MapParser extends DefaultHandler
{
    // Our SAXParser and its factory
    private SAXParser           parser          = null;
    private SAXParserFactory    parserFactory   = null;
    
    // Our Schema and its factory
    private Schema              schema          = null;
    private SchemaFactory       schemaFactory   = null;
    
    // For storing temporary values
    private Cell tempCell;
    private String tempValue;
    
    // We are using the XML Schema (W3C)
    private static final String XML_SCHEMA = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    
    // Constructor
    public MapParser()
    {
        // Load map Schema
        //loadSchema();
        
        // Load map Parser
        loadParser();
    }
    
    // Set up our schema
    @SuppressWarnings("unused")
    private void loadSchema()
    {
        // Only need to do this once
        if (schema != null) return;
        
        // Attempt to create our Map schema
        try
        {
            // Load factory
            schemaFactory = SchemaFactory.newInstance(XML_SCHEMA);
            
            // Load Map schema
            schema = schemaFactory.newSchema(new File("maps/mapSchema.xsd"));
        } 
        
        // Problem loading schema
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    // Set up our SAX Parser
    private void loadParser()
    {
        // Schema must be loaded first
        //if (schema == null || parser != null) return;
        if (parser != null) return;
        
        // Get the SAXParser factory
        parserFactory = SAXParserFactory.newInstance();
        
        // Set our schema to XSD
        //parserFactory.setSchema(schema);
        
        // We want to be aware of namespaces
        parserFactory.setNamespaceAware(true);
        
        // Attempt to parse the map file
        try {
            // Get our SAX Parser object
            parser = parserFactory.newSAXParser();
        }
        
        // Configuration Exception
        catch(ParserConfigurationException e) {
            e.printStackTrace();
        } 
        
        // SAX Parser Exception
        catch (SAXException e) {
            e.printStackTrace();
        }
    }
    
    // Parses an XML map file
    public void parseMap(String file)
    {        
        // Attempt to parse the map file
        try {
            System.out.print("Parsing file '" + file + "' ...");
            
            // Parse the map, using this class for callback handling
            parser.parse(file, this);
            
            System.out.println("done.");
        }
        
        // SAX Parser Exception
        catch(SAXException e) {
            e.printStackTrace();
        }
        
        // File not found
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Called when the Parser starts parsing the Current XML File.
    public void startDocument() throws SAXException {}

    // Called when the Parser Completes parsing the Current XML File.
    public void endDocument() throws SAXException {}

    // Receive notification of the start of an element. 
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        // Reset tempValue
        tempValue = "";
        
        // We are defining the map
        if (qName.equalsIgnoreCase("map"))
        {            
            // Make sure we have row and column attributes
            if ((attributes.getValue("cellsize") != null) && 
                    (attributes.getValue("rows") != null) &&
                    (attributes.getValue("columns") != null))
            {
                int r, c, s;
                
                // Store row and column cell size
                r = Integer.parseInt(attributes.getValue("rows"));
                c = Integer.parseInt(attributes.getValue("columns"));
                s = Integer.parseInt(attributes.getValue("cellsize"));
                
                // Create the Grid system
                Grid.createGrid(r, c, s);
            }
        }
        
        // We have a new cell element
        else if (qName.equalsIgnoreCase("cell"))
        {            
            // Make sure we have row and column attributes
            if ((attributes.getValue("row") != null) &&
                    (attributes.getValue("column") != null))
            {
                int r, c;
                
                // Store row and column
                r = Integer.parseInt(attributes.getValue("row"));
                c = Integer.parseInt(attributes.getValue("column"));
                
                // Create a new cell and add it to the cell list
                tempCell = new Cell(r, c);
            }
        }
    }
    
    // Receive notification of character data inside an element.
    public void characters(char buf[], int offset, int len) throws SAXException
    {
        tempValue = new String(buf, offset, len).trim();
    }

    // Receive notification of the end of an element. 
    public void endElement(String uri, String localName, String qName) throws SAXException
    {        
        // Make sure we have a cell to work with
        if (tempCell != null)
        {            
            // Toggle the cell's spawnability
            if (qName.equalsIgnoreCase("spawn"))
            {
                if (tempValue.equalsIgnoreCase("true")) tempCell.setSpawn(true);
                else tempCell.setSpawn(false);
            }
            
            // Toggle the cell's exitability
            else if (qName.equalsIgnoreCase("goal"))
            {
                if (tempValue.equalsIgnoreCase("true")) tempCell.setGoal(true);
                else tempCell.setGoal(false);
            }
            
            // Toggle the cell's spawnability
            else if (qName.equalsIgnoreCase("playable"))
            {
                if (tempValue.equalsIgnoreCase("true")) tempCell.setPlayable(true);
                else tempCell.setPlayable(false);
            }
            
            // Exiting a cell element
            else if (qName.equalsIgnoreCase("cell"))
            {
                // Add the new cell to the list
                Grid.addCell(tempCell);
                
                // Get rid of the reference
                tempCell = null;
            }
        }
    }

    /*
     * In the XML File if the parser encounters a Processing Instruction which is
     * declared like this  <?ProgramName:BooksLib QUERY="author, isbn, price"?> 
     * Then this method is called where Target parameter will have
     * "ProgramName:BooksLib" and data parameter will have  QUERY="author, isbn,
     *  price". You can invoke a External Program from this Method if required. 
     */
    public void processingInstruction(String target, String data) throws SAXException {}
    
    /**
     * 
     * Error Handling
     * 
     */
    
    // Fatal parse error, document is unreliable (not well formed or valid)
    public void fatalError(SAXParseException e) throws SAXException
    {
        System.out.println(e.toString());
    }
    
    // Parse error (usually a validation error)
    public void error(SAXParseException e) throws SAXException
    {
        System.out.println(e.toString());
    }
    
    // Parse warnings
    public void warning(SAXParseException e) throws SAXException
    {
        System.out.println(e.toString());
    }
}