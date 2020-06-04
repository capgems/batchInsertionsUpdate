<h1> execution </h1>
  
   <p> please check the application properties before running the application. </p>
   <p> update your application.properties file to make sure you are running it for insert or update. </p>
  
  <h3> Configuring your application.properties </h3>
         <p> please update the application.properties file before running the application, if you want to insert the records 
         into the database make sure you have done step 1, to update the records follow step 2.</p>
       <h4><strong>Step 1: </strong></h4>  <h5> insertion of records </h5>
           <p> make sure this property spring.datasource.initialization-mode= is set to <strong> 'always' </strong> </p>
           <p> make sure this property config.operation.sql= is set to <strong> 'insert' </strong> </p>
      <h4><strong>Step 2: </strong></h4> <h5> updating of records </h5>
           <p> make sure this property spring.datasource.initialization-mode= is set to <strong> 'never' </strong> </p>
           <p> make sure this property config.operation.sql= is set to <strong> 'update' </strong> </p>
           
 <h1> you can see the time metrics in the logs after completing the job like insert/update. </h1>          
           
           
  
   
  
