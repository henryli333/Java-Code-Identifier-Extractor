package output;

import com.google.gson.Gson;
import model.ASTIdentifierNode;
import output.interfaces.OutputFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonOutputFormatter implements OutputFormatter {

    @Override
    public String format(ASTIdentifierNode root) {
        Gson gson = new Gson();

        List<ASTIdentifierNode> childList = new ArrayList<>(root.getChildren());
        childList.sort(ASTIdentifierNode::ordinalCompare);

        return gson.toJson(root);
    }

    @Override
    public void close() throws IOException {

    }
}
