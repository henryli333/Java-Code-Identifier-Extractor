package output;

import com.google.gson.Gson;
import model.ASTIdentifierNode;
import output.interfaces.IOutputFormatter;

import java.util.ArrayList;
import java.util.List;

public class JsonOutputFormatter implements IOutputFormatter {

    @Override
    public String print(ASTIdentifierNode root) {
        Gson gson = new Gson();

        List<ASTIdentifierNode> childList = new ArrayList<>(root.getChildren());
        childList.sort(ASTIdentifierNode::ordinalCompare);

        return gson.toJson(root);
    }

}
