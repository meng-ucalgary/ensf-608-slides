// ENSF 608
// Demo java program for connecting with databases


import java.sql.*;

public class Registration{

    public final String DBURL;
    public final String USERNAME;
    public final String PASSWORD;    
    
//Optional to include, but recommended    
    private Connection dbConnect;
    private ResultSet results;
    
    public Registration(String url, String user, String pw){

    // Database URL
    this.DBURL = url;

    //  Database credentials
    this.USERNAME = user;
    this.PASSWORD = pw;
    }

//Must create a connection to the database, no arguments, no return value    
    public void initializeConnection(){
        
        try{
            dbConnect = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    String getDburl(){
        return this.DBURL;
    }

    String getUsername(){
        return this.USERNAME;
    }
    
    String getPassword(){
        return this.PASSWORD;
    }

    
    public String selectAllNames(String tableName){

        StringBuffer foundNames = new StringBuffer();
        
        try {                    
            Statement myStmt = dbConnect.createStatement();
            
            // Execute SQL query
            results = myStmt.executeQuery("SELECT * FROM " + tableName);
            
            // Process the results set
            while (results.next()){
                foundNames.append(results.getString("LName") + ", " + results.getString("FName"));
                foundNames.append('\n');
            }
            foundNames.deleteCharAt(foundNames.length()-1);
            
            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return foundNames.toString();
    }

    public String showStudios(){
        
        StringBuffer studioNames = new StringBuffer();
        
        try {                    
            Statement myStmt = dbConnect.createStatement();
            
            // Execute SQL query
            results = myStmt.executeQuery("SELECT * FROM studio");
                        
            // Process the results set
            while (results.next()){
                studioNames.append(results.getString("Name"));
                studioNames.append('\n');
            }
            
            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        studioNames.deleteCharAt(studioNames.length()-1);
        
        return studioNames.toString();
        
    }
    
    
    public void insertNewCompetitor(String id, String lName, String fName, int age, String instrument, String teacherID){

        if(!validateTeacher(teacherID)){
            throw new IllegalArgumentException("Student must have a registered teacher.");
        }

        if(age < 5 || age > 18){
            throw new IllegalArgumentException("Student must be between the ages of 5 and 18.");
        }

        try {
            
            String query = "INSERT INTO competitor (CompetitorID, LName, FName, Age, Instrument, TeacherID) VALUES (?,?,?,?,?,?)";
            PreparedStatement myStmt = dbConnect.prepareStatement(query);
            
            myStmt.setString(1, id);
            myStmt.setString(2, lName);
            myStmt.setString(3, fName);
            myStmt.setInt(4, age);
            myStmt.setString(5, instrument);
            myStmt.setString(6, teacherID);
            
            int rowCount = myStmt.executeUpdate();
            
            myStmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    

    public void registerNewTeacher(String id, String lName, String fName, String phone, String studio, String studioPhone, String studioAddress){
        
        if(validateTeacher(id)){
            throw new IllegalArgumentException("Teacher is already registered.");
        }

        if(!validateStudio(studio)){
            try {
                String query = "INSERT INTO studio (Name, Phone, Address) VALUES (?,?,?)";
                PreparedStatement myStmt = dbConnect.prepareStatement(query);
            
                myStmt.setString(1, studio);
                myStmt.setString(2, studioPhone);
                myStmt.setString(3, studioAddress);
            
                int rowCount = myStmt.executeUpdate();
            
                myStmt.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        try {
            
            String query = "INSERT INTO teacher (TeacherID, LName, FName, Phone, StudioName) VALUES (?,?,?,?,?)";
            PreparedStatement myStmt = dbConnect.prepareStatement(query);
            
            myStmt.setString(1, id);
            myStmt.setString(2, lName);
            myStmt.setString(3, fName);
            myStmt.setString(4, phone);
            myStmt.setString(5, studio);
            
            int rowCount = myStmt.executeUpdate();
            
            myStmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }        
    
    public boolean validateTeacher(String teacherID){
        
        boolean validTeacher = false;
                    
        try {                    
            Statement myStmt = dbConnect.createStatement();
            
            // Execute SQL query
            results = myStmt.executeQuery("SELECT * FROM teacher");
            
            // Process the results set
            while (results.next()){
                if(results.getString("TeacherID").equals(teacherID))
                    validTeacher = true;
            }
            
            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return validTeacher;

    }    
    
    public boolean validateStudio(String studioName){
        
        boolean studioExists = false;
                    
        try {                    
            Statement myStmt = dbConnect.createStatement();
            
            // Execute SQL query
            results = myStmt.executeQuery("SELECT * FROM studio");
            
            // Process the results set
            while (results.next()){
                if(results.getString("Name").equals(studioName))
                    studioExists = true;
            }
            
            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return studioExists;

    }    
    
    public void deleteCompetitor(String id){
                    
        try {
            String query = "DELETE FROM competitor WHERE CompetitorID = ?";
            PreparedStatement myStmt = dbConnect.prepareStatement(query);

            myStmt.setString(1, id);
                        
            int rowCount = myStmt.executeUpdate();
            
            myStmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }    

    public void deleteTeacher(String id){
                    
        try {
            String query = "DELETE FROM teacher WHERE TeacherID = ?";
            PreparedStatement myStmt = dbConnect.prepareStatement(query);

            myStmt.setString(1, id);
                        
            int rowCount = myStmt.executeUpdate();
            
            myStmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }     

    public void close() {
        try {
            results.close();
            dbConnect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {

        //Use whatever account information you want on your system.
        Registration myJDBC = new Registration("jdbc:mysql://localhost/competition","Marasco","ensf409");
        
        myJDBC.initializeConnection();
        
        System.out.println("------------------------------");
        System.out.println("***Testing getter methods:***");
        System.out.println(myJDBC.getDburl());
        System.out.println(myJDBC.getUsername());
        System.out.println(myJDBC.getPassword());
        
        System.out.println("------------------------------");
        System.out.println("***Printing list of competitors:***");
        System.out.println(myJDBC.selectAllNames("competitor"));
        System.out.println("------------------------------");
        System.out.println("***Printing list of teachers:***");
        System.out.println(myJDBC.selectAllNames("teacher"));

        System.out.println("------------------------------");
        System.out.println("***Printing list of studios:***");        
        System.out.println(myJDBC.showStudios());

        System.out.println("------------------------------");
        System.out.println("***Now testing insert statements...***");                
        myJDBC.insertNewCompetitor("234", "Robertson", "Ebba", 15, "Trombone", "9202");
        System.out.println("Competitor #234 Robertson, Ebba should now be added.");

        //Insertion should fail and throw an IllegalArgumentException
        try{
            myJDBC.insertNewCompetitor("678", "Jordan", "Ali", 10, "Oboe", "9807");
        }catch(IllegalArgumentException e){
            System.out.println("Sucessfully threw exception when no registered teacher can be found for a competitor.");
        }
        
        //Insertion should fail and throw an IllegalArgumentException
        try{
            myJDBC.insertNewCompetitor("654", "Smyth", "Ace", 4, "Oboe", "1012");
        }catch(IllegalArgumentException e){
            System.out.println("Sucessfully threw exception when competitor is outside valid age range.");
        }
        
        //Inserts a new teacher into the database
        myJDBC.registerNewTeacher("2849", "Lacombe", "Trent", "403-764-3323", "Lyrica", "403-357-4457", "62 Heron Drive NW");        
        System.out.println("Teacher #2849 Lacombe, Trent should now be added.");

        //Insertion should fail and throw an Illegal Argument Exception
        try{
            myJDBC.registerNewTeacher("0077", "Belma", "Doug", "403-646-3302", "Harmonius Learning", "403-954-5232", "294 Spruce Lane NE");        
        }catch(IllegalArgumentException e){
            System.out.println("Sucessfully threw exception teacher is already registered.");
        }
        
        //A new studio should be added when the teacher's studio is not yet in the database
        //Check to make sure the database has been updated with a new teacher AND a new studio
        myJDBC.registerNewTeacher("6887", "McMurray", "Jill", "587-101-8790", "Music Notes", "587-746-9980", "99 Pinewood Way NE");        
        System.out.println("Teacher #6887 McMurray, Jill should now be added.");
        System.out.println("Studio Music Notes should now be added.");

        System.out.println("------------------------------");
        System.out.println("***Now testing delete statements...***");                        

        //Deletes the specified competitor from the database
        myJDBC.deleteCompetitor("205");
        System.out.println("Competitor #205 Kamilla, Mala should now be deleted.");

        //Deletes the specified teacher from the database
        myJDBC.deleteTeacher("3575");
        System.out.println("Teacher #3575 Steele, Lucio should now be deleted.");
        
        //Demonstrate that all Statement, ResultSet, and Connection objects are closed.  
        //Flexible implementation- likely Statements/PreparedStatements are closed in the methods that use them and
        //ResultSet/Connection objects are likely closed in some sort of close method.
        myJDBC.close();

        System.out.println("------------------------------");
        System.out.println("***End of tests.***");                        
    }
}
