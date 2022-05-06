import com.makolab.graphelements.*;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;

import java.net.URI;
import java.net.URISyntaxException;

public class Rdf4jReader {
    public static Graph readGraph(Model g) throws URISyntaxException {
        var graph = new Graph();
        for (var statement : g) {
            Node s;
            if (statement.getSubject().isBNode()) {
                s = new BlankNode(statement.getSubject().stringValue());
            }
            else if (statement.getSubject().isIRI()){
                s = new IriNode(statement.getSubject().stringValue());
            }
            else {
                throw new IllegalArgumentException("Unknown node type");
            }
            Node o;
            if (statement.getObject().isBNode()) {
                o = new BlankNode(statement.getObject().stringValue());
            } else if (statement.getObject().isIRI()) {
                o = new IriNode(statement.getObject().stringValue());
            } else if (statement.getObject().isLiteral()) {
                var l = (Literal)statement.getObject();
                o = new LiteralNode(l.getLabel(), l.getDatatype(), l.getLanguage());
            }
            else {
                throw new IllegalArgumentException("Unknown node type");
            }
            graph.addTriple(s, new URI(statement.getPredicate().stringValue()), o);
        }
        return graph;
    }
}
