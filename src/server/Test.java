package server;

import java.sql.SQLException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        try {
            DataBaseHelper.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> arStr = new ArrayList<>();
        arStr = DataBaseHelper.receiveFromDB("Alex");
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < arStr.size(); i++)
        {
            stringBuffer.append(arStr.get(i));
        }
        System.out.println(stringBuffer.toString());
        String strFromHistory = stringBuffer.toString();
        String[] elements = strFromHistory.split("/beginHistoryStr");
        ArrayList<String> stringForHistory = new ArrayList<>();
        for(int i=1; i<elements.length;i++)
        {
            String[] elementsByValue;
            elementsByValue = (elements[i]).split("/`");
            //stringForHistory.add()
            if(elementsByValue[2].equalsIgnoreCase("ALL"))
            {
                stringForHistory.add(elementsByValue[0] + " " + elementsByValue[1] + ": " + elementsByValue[3]);
            }else {
                stringForHistory.add(elementsByValue[0] + " " + elementsByValue[1] + " to " + elementsByValue[2] +": " + elementsByValue[3]);
            }

        }
        System.out.println(stringForHistory);
        System.out.println("jjj");
    }
}
