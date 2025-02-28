package patientCare;

import java.net.*;

import java.io.*;

 
public class EchoServer {
    public static void main(String[] args) throws IOException {
         
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
         
        int portNumber = Integer.parseInt(args[0]);
         
        try (
            ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0]));
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);    
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            String outputLine;
            ResponseProtocol controler = new ResponseProtocol();
            while ((inputLine = in.readLine()) != null) {
            	outputLine = controler.comunicate(inputLine);
                out.println(outputLine);
                if(outputLine.equals("error")) break; //closes Socket after ending try
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}