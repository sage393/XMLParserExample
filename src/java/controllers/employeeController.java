/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Employee;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import models.EmployeeFacade;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author sam
 */
@ManagedBean(name = "employeeController")
@SessionScoped
public class employeeController implements Serializable {

    @EJB
    EmployeeFacade employeeFacade;

    private List<Employee> lstEmployee;
    private Employee selectedEmployee;
    
    private ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

    @PostConstruct
    public void init() {
        selectedEmployee = new Employee();
        listEmployee();
    }

    private List<Employee> listEmployee() {
        return lstEmployee = employeeFacade.findAll();
    }
    
    public void parseEmployeesFromXml() {
        try {
            //Get Docuemnt Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            //Build Document
            String path = sc.getRealPath("xml/employees.xml");
            Document document = builder.parse(new File(path));

            //Normalize the XML Structure; It's just too important !!
            document.getDocumentElement().normalize();

            //Here comes the root node
            Element root = document.getDocumentElement();
            System.out.println(root.getNodeName());

            //Get all employees
            NodeList nList = document.getElementsByTagName("employee");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                //System.out.println("");    //Just a separator
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    
                    Employee employee = new Employee();
                    employee.setIdEmployee(Integer.parseInt(eElement.getAttribute("id")));
                    employee.setFirstName(eElement.getElementsByTagName("firstName").item(0).getTextContent());
                    employee.setLastName(eElement.getElementsByTagName("lastName").item(0).getTextContent());
                    employee.setLocation(eElement.getElementsByTagName("location").item(0).getTextContent());
                    
                    employeeFacade.edit(employee);
                    listEmployee();
                }
            }
            
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
            System.out.println("Error parsing XML: " + e);
        }
    }

    public List<Employee> getLstEmployee() {
        return lstEmployee;
    }

    public void setLstEmployee(List<Employee> lstEmployee) {
        this.lstEmployee = lstEmployee;
    }

    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

}
