package org.ivoa.vodml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import org.ivoa.vodml.graph.DataTypeNode;
import org.ivoa.vodml.graph.ElementNode;
import org.ivoa.vodml.graph.ModelGraph;
import org.ivoa.vodml.graph.ModelNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.RoleNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.xsd.jaxb.Attribute;
import org.ivoa.vodml.xsd.jaxb.Composition;
import org.ivoa.vodml.xsd.jaxb.DataType;
import org.ivoa.vodml.xsd.jaxb.Enumeration;
import org.ivoa.vodml.xsd.jaxb.Model;
import org.ivoa.vodml.xsd.jaxb.ModelImport;
import org.ivoa.vodml.xsd.jaxb.ObjectType;
import org.ivoa.vodml.xsd.jaxb.PrimitiveType;
import org.ivoa.vodml.xsd.jaxb.ReferableElement;
import org.ivoa.vodml.xsd.jaxb.Reference;
import org.ivoa.vodml.xsd.jaxb.Relation;
import org.ivoa.vodml.xsd.jaxb.Type;

public class PathGenerator {

	public enum WriteMode {
		HTML("H"), ASCII("A"), XML("X");

		private final String value;

		WriteMode(final String v) {
			value = v;
		}

		public final static WriteMode fromValue(final String v) {
			for (WriteMode c : WriteMode.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException(
					"WriteMode.fromValue : No enum const for the value : " + v);
		}
	}

	public class Path extends Stack<ReferableElement> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8448650398120845959L;
		public static final String MODEL_SEP = ":";
		public static final String PACKAGE_SEP = "/";
		public static final String TYPE_SEP = ".";
		public static final String ROLE_SEP = ".";
		private PrintStream out;
		private Hashtable<String, DataType> cache;
		private boolean writeWhenPush = true;

		private WriteMode writeMode = WriteMode.ASCII;

		public Path(PrintStream _out) {
			this(_out, WriteMode.ASCII.value);
		}

		public Path(PrintStream _out, String _writeMode) {
			this.out = _out;
			this.writeMode = WriteMode.fromValue(_writeMode);
			cache = new Hashtable<String, DataType>();
		}

		@Override
		public String toString() {
			return asPath();
		}

		public String asPath() {
			StringBuilder sb = new StringBuilder();
			String currentSeparator = "";
			for (ReferableElement e : this) {
				sb.append(currentSeparator);
				/*
				if (e instanceof Model) {
					sb.append(Utype.utypeFor(e));
					currentSeparator = MODEL_SEP;
				} else */ 
				if (e instanceof org.ivoa.vodml.xsd.jaxb.Package) {
					sb.append(((org.ivoa.vodml.xsd.jaxb.Package) e).getName())
							.append(PACKAGE_SEP);
					currentSeparator = "";
				} else if (e instanceof Type) {
					sb.append(((Type) e).getName());
					currentSeparator = TYPE_SEP;
				} else if (e instanceof Attribute) {
					sb.append(((Attribute) e).getName());
					currentSeparator = ROLE_SEP;
//				} else if (e instanceof Container) {
//					sb.append("CONTAINER");
//					currentSeparator = ROLE_SEP;
				} else if (e instanceof Relation) {
					sb.append(((Relation) e).getName());
					currentSeparator = ROLE_SEP;
				}
			}
			return sb.toString();
		}

		private String rootURL(String utype) {
			String modelURL = vodml.getModelUrl(utype);
			return modelURL.replaceFirst("vo-dml.xml", "html");
		}

		private void link(String utype, String name) {
			out.append("<a href=\"").append(rootURL(utype)).append("#")
					.append(utype).append("\">")
					.append(name == null ? utype : name).append("</a>");
		}

		private void utypeLink(String utype) {
			// out.append("[");
			link(utype, utype);
			// out.append("]");
		}

		public void asUtypelinkedPath() {
			String currentSeparator = "";
			for (ReferableElement e : this) {
				out.append(currentSeparator);
				utypeLink(VODMLREF.vodmlrefFor(e,model));
				currentSeparator = "/";
			}
		}

		public void asHyperlinkedPath() {
			String currentSeparator = "";
			for (ReferableElement e : this) {
				String utype = VODMLREF.vodmlrefFor(e,model);
				out.append(currentSeparator);
				/*
				if (e instanceof Model) {
					link(utype, null);
					currentSeparator = ":";
				} else*/
				if (e instanceof org.ivoa.vodml.xsd.jaxb.Package) {
					link(utype, ((org.ivoa.vodml.xsd.jaxb.Package) e).getName());
					currentSeparator = "/";
				} else if (e instanceof Type) {
					link(utype, ((Type) e).getName());
					currentSeparator = ".";
				} else if (e instanceof Attribute) {
					link(utype, ((Attribute) e).getName());
					currentSeparator = ".";
//				} else if (e instanceof Container) {
//					link(utype, "CONTAINER");
//					currentSeparator = ROLE_SEP;
				} else if (e instanceof Relation) {
					link(utype, ((Relation) e).getName());
					currentSeparator = ".";
				}
			}
		}

		/**
		 * Bunch of XML elements.
		 * 
		 * @return
		 */
		public String asXML() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("<path expression=\"%s\">", asPath()))
					.append(NL);
			ArrayList<String> l = new ArrayList<String>();
			for (ReferableElement e : this) {
				sb.append("<path-element type=\"")
						.append(e.getClass().getSimpleName())
						.append("\" vodmlref=\"").append(VODMLREF.vodmlrefFor(e,model))
						.append("\"");
				if (e == this.lastElement())
					sb.append("/>").append(NL);
				else {
					sb.append(">").append(NL);
					l.add("</path-element>");
				}
			}
			for (int i = l.size() - 1; i >= 0; i--)
				sb.append(l.get(i)).append(NL);
			sb.append("</path>").append(NL);
			return sb.toString();
		}

		@Override
		public ReferableElement push(ReferableElement item) {
			// TODO Auto-generated method stub
			super.push(item);
			boolean foundCycle = false;
			if (item instanceof Attribute) {
				String utype = ((Attribute) item).getDatatype().getVodmlRef();
				TypeNode t = vodml.findType(utype);
				if (t instanceof DataTypeNode) {
					DataType odt = cache.get(t.getVODMLREF());
					if (odt != null) {
						foundCycle = true;
						System.out.printf("=======>  Found cycle at '%s'\n",
								asPath());
					} else
						cache.put(utype, ((DataTypeNode) t).getDataType());
				}
			}
			if (writeWhenPush)
				write();
			if (foundCycle)
				return null;
			else
				return item;
		}

		@Override
		public synchronized ReferableElement pop() {
			ReferableElement el = super.pop();
			if (el instanceof Attribute) {
				cache.remove(((Attribute) el).getDatatype().getVodmlRef());
			}
			return el;
		}

		private void write() {
			switch (writeMode) {
			case HTML:
				String path = asPath();
				out.append("<tr>").append(NL)
						.append("<td class=\"feature-detail\">");
				out.append("<a name=\"").append(path).append("\"></a>");
				out.append(path).append("</td>").append(NL)
						.append("<td class=\"feature-detail\">");
				asHyperlinkedPath();
				out.append("</td>").append(NL)
						.append("<td class=\"feature-detail\">");
				asUtypelinkedPath();
				out.append("</td>").append(NL).append("</tr>").append(NL);
				break;
			case XML:
				out.append(asXML()).append(NL);
				break;
			default: // case ASCII:
				out.append(asPath()).append(NL);
				break;
			}
		}
	}

	public final String NL;
	private static String NEWLINE_n = "\n", NEWLINE_rn = "\r\n";
	private static final String PACKAGE_SEPARATOR = "/", DOT_SEPARATOR = ".",
			MODEL_SEPARATOR = ":";
	private VODMLManager vodml;
	private ModelGraph graph;
	private Model model;
	private Path path;

	public PathGenerator() {
		this(NEWLINE_rn);
	}

	public PathGenerator(String nl) {
		NL = NEWLINE_rn.equals(nl) ? NEWLINE_n : NEWLINE_n;
	}

	public void generatPaths(Model model, PrintStream out, String mode)
			throws IOException {
		path = new Path(out, mode);

		for (ObjectType ot : model.getObjectType())
			handle(ot);
		for (DataType ot : model.getDataType())
			handle(ot, true);
		for (org.ivoa.vodml.xsd.jaxb.Package p : model.getPackage())
			handle(p);

	}

	/**
	 * Accepts an expression and tries to iterpret in in terms of VO-DML data
	 * models.<br/>
	 * Produces a Path as result.
	 * 
	 * @param pathExpression
	 * @return
	 */
	public void interpret(String pathExpression, VODMLManager vodml,
			PrintStream out) {
		out.println("Interpreting path:" + pathExpression);

		this.vodml = vodml;
		Path path = new Path(out);
		path.writeWhenPush = false;
		// extract model from prefix
		try {
			String pe = pathExpression;
			String sep = MODEL_SEPARATOR;
			int start = 0;
			int end = pe.indexOf(sep, start);
			if (end == -1) // TODO throw i
				throw new IllegalArgumentException(String.format(
						"path expression '%s' is invalid, must have a prefix",
						pe));

			String word = pe.substring(start, end);
			start = end + 1;

			ElementNode currentElement = vodml.findReferableElement(word);
			if (currentElement == null
					|| !(currentElement instanceof ModelNode))
				throw new IllegalArgumentException(String.format(
						"Unable to find model for prefix '%s'", word));
			path.push(currentElement.getElement());

			sep = PACKAGE_SEPARATOR;
			while (true) {
				int index = pe.indexOf(sep, start);
				if (index == -1) // no packages
					break;
				end = index;
				word = pe.substring(start, end);
				start = end + 1;

				String utype = currentElement.childForName(word);
				if (utype == null)
					throw new IllegalArgumentException(
							String.format(
									"Element '%s' does not have a child with name '%s'",
									currentElement.getVODMLREF(), word));
				currentElement = vodml.findReferableElement(utype);
				path.push(currentElement.getElement());
			}
			// types with roles from now on, split on '.'
			String error = null;

			if (start < pe.length()) {
				String[] words = pe.substring(start).split("\\.");
				String utype = currentElement.childForName(words[0]);
				if (utype == null)
					throw new IllegalArgumentException(
							String.format(
									"Element '%s' does not have a child with name '%s'",
									path.toString(), words[0]));
				else {
					currentElement = vodml.findReferableElement(utype);
					path.push(currentElement.getElement());

					TypeNode currentType = (TypeNode) currentElement;
					for (int i = 1; i < words.length; i++) {
						ArrayList<String> utypes = findChildForType(
								currentType, words[i]);
						if (utypes.size() == 1) // 1 utype found
						{
							utype = utypes.get(0);
							currentElement = vodml
									.findReferableElement(utype);
							path.push(currentElement.getElement());
							if (currentElement instanceof RoleNode) //
								currentType = ((RoleNode) currentElement)
										.getDatatype();
						} else if (utypes.size() == 0)
							throw new IllegalArgumentException(
									String.format(
											"Element '%s' does not have a child with name '%s'",
											path.toString(), words[i]));
						else {
							StringBuffer sb = new StringBuffer();
							sb.append(String
									.format("path up to '%s' is of type '%s' and has multiple options for child name '%s':",
											path.toString(),currentType.getVODMLREF(), words[i]));
							sep = "\n- ";
							for (String s : utypes) {
								sb.append(sep).append(s);
							}
							throw new IllegalArgumentException(sb.toString());
						}
					}
				}
			}
			path.writeMode = WriteMode.XML;
			out.println(path.asXML());

		} catch (IllegalArgumentException e) {
			out.println(e.getMessage());
		}
		out.flush();
	}

	/**
	 * 
	 * @param tn
	 * @param name
	 * @return
	 */
	private ArrayList<String> findChildForType(TypeNode tn, String name) {
		if (tn == null)
			return null;
		ArrayList<String> utypes = new ArrayList<String>();
		String utype = tn.childForName(name);
		if (utype != null)
			utypes.add(utype);
		findChildInBaseType(tn.getSuperType(), name, utypes);
		findChildInSubTypes(tn, name, utypes);
		return utypes;
	}

	private void findChildInBaseType(TypeNode tn, String name,
			ArrayList<String> utypes) {
		if (tn == null)
			return;
		String utype = tn.childForName(name);
		if (utype != null)
			utypes.add(utype);
		findChildInBaseType(tn.getSuperType(), name, utypes);
	}

	private void findChildInSubTypes(TypeNode tn, String name,
			ArrayList<String> utypes) {
		Iterator<TypeNode> scs = tn.getSubClasses();
		if (scs == null)
			return;
		while (scs.hasNext()) {
			TypeNode sc = (TypeNode) scs.next();
			String utype = sc.childForName(name);
			if (utype != null)
				utypes.add(utype);
			findChildInSubTypes(sc, name, utypes);
		}
	}

	private void handle(org.ivoa.vodml.xsd.jaxb.Package p) {
		path.push(p);
		for (ObjectType ot : p.getObjectType())
			handle(ot);
		for (DataType dt : p.getDataType())
			handle(dt, true);
		for (org.ivoa.vodml.xsd.jaxb.Package cp : p.getPackage())
			handle(cp);
		path.pop();
	}
/*
	private void handleContainer(ObjectType t) {
		if (t.getContainer() != null)
			handle(t.getContainer());
		else if (t.getExtends() != null)
			handleContainer(((ObjectTypeNode) vodml.findType(t.getExtends()
					.getVodmlRef())).getObjectType());
	}
*/
	private void handle(ObjectType ot) {
		path.push(ot);
//		handleContainer(ot);
		handleAttributes(ot);
		handleReferences(ot);
		handleCollections(ot);
		path.pop();
	}

	private void handle(DataType dt, boolean asRoot) {
		if (asRoot) {
			path.push(dt);
		}
		handleAttributes(dt);
		handleReferences(dt);
		if (asRoot)
			path.pop();
	}

	private void handleAttributes(ObjectType ot) {
		if (ot.getExtends() != null)
			handleAttributes(((ObjectTypeNode) vodml.findType(ot.getExtends()
					.getVodmlRef())).getObjectType());
		for (Attribute at : ot.getAttribute())
			handle(at);
	}

	private void handleAttributes(DataType dt) {
		if (dt.getExtends() != null)
			handleAttributes(((DataTypeNode) vodml.findType(dt.getExtends()
					.getVodmlRef())).getDataType());
		for (Attribute at : dt.getAttribute())
			handle(at);
	}

	private void handleReferences(DataType dt) {
		if (dt.getExtends() != null)
			handleReferences(((DataTypeNode) vodml.findType(dt.getExtends()
					.getVodmlRef())).getDataType());
		for (Reference r : dt.getReference())
			handle(r);
	}

	private void handleReferences(ObjectType dt) {
		if (dt.getExtends() != null)
			handleReferences(((ObjectTypeNode) vodml.findType(dt.getExtends()
					.getVodmlRef())).getObjectType());
		for (Reference r : dt.getReference())
			handle(r);
	}

	private void handleCollections(ObjectType ot) {
		if (ot.getExtends() != null)
			handleCollections(((ObjectTypeNode) vodml.findType(ot.getExtends()
					.getVodmlRef())).getObjectType());
		for (Composition c : ot.getComposition())
			handle(c);
	}

	private void handle(Attribute at) {
		ReferableElement el = path.push(at);
		if (el == at) {
			TypeNode t = graph.getType(at);
			if (t instanceof DataTypeNode)
				handle(((DataTypeNode) t).getDataType(), false);
			Iterator<TypeNode> scs = t.getSubClasses();
			while (scs.hasNext()) {
				TypeNode sc = scs.next();
				if (sc instanceof DataTypeNode)
					handle(((DataTypeNode) sc).getDataType(), false);
			}

		}
		path.pop();
	}

	private void handle(Relation r) {
		path.push(r);
		path.pop();
	}

	/*
	private void handle(Container r) {
		path.push(r);
		path.pop();
	}
*/
	private static void printUsage() {
		System.out
				.println(" usage I: org.ivoa.vodml.PathGenerator I <vodml-url> <path-expression>");
		System.out
				.println("usage II: org.ivoa.vodml.PathGenerator G <vodml-file> <oufile> <mode>");
		System.out.printf("\tcommand : ASCII\n", WriteMode.ASCII.value);
		System.out.printf("\tmode=%s : ASCII\n", WriteMode.ASCII.value);
		System.out.printf("\tmode=%s : HTML Table", WriteMode.HTML.value);
		System.out
				.printf("\tmode=%s : XML representation", WriteMode.XML.value);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			return;
		}
		if ("G".equals(args[0]))
			generatePaths(args);
		else if ("I".equals(args[0]))
			interpretPath(args);
		else
			printUsage();
	}

	private static void interpretPath(String[] args) {
		if (args.length != 3) {
			printUsage();
			return;
		}
		String modelURL = args[1];
		String path = args[2];
		try {
			PathGenerator pg = new PathGenerator();
			pg.interpret(path, new VODMLManager(new RemoteVODMLRegistry(), modelURL), System.out);
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	private static void generatePaths(String[] args) {
		if (args.length != 4) {
			printUsage();
			return;
		}
		String vodmlFile = args[1];
		String outFile = args[2];
		WriteMode mode = WriteMode.fromValue(args[3]);
		try {
			File f = new File(vodmlFile);
			String url = f.toURI().toURL().toExternalForm();
			PathGenerator pg = new PathGenerator();
			switch (mode) {
			case HTML:
				pg.generateHTML(url, outFile);
				break;
			case XML:
				pg.generateXML(url, outFile);
				break;
			case ASCII:
				pg.generateASCII(url, outFile);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateHTML(String url, String outFile) throws IOException {
		init(url);

		PrintStream out = new PrintStream(new File(outFile));
		out.append(
				"<table style=\"border-style:solid;border-width:1px;\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">")
				.append(NL);
		out.append(
				"<tr><td class=\"feature-heading\">PATH</td><td class=\"feature-heading\">Linked PATH</td><td class=\"feature-heading\">Corresopnding UTYPE path</td></tr>")
				.append(NL);
		generatPaths(model, out, WriteMode.HTML.value);
		out.append("</table>");
		out.flush();
		out.close();
	}

	public void generateXML(String url, String outFile) throws IOException {
		init(url);

		PrintStream out = new PrintStream(new File(outFile));
		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NL);
		out.append("<modelPaths><model>");
		out.append("<url>").append(graph.getModelURL()).append("</url>");
		out.append("<prefix>").append(model.getName())
				.append("</prefix></model>").append(NL);
		for (ModelImport mp : model.getImport()) {
			ModelGraph mg = vodml.findModelForURL(mp.getUrl());
			out.append("<model>");
			out.append("<url>").append(mp.getUrl()).append("</url>");
			out.append("<prefix>").append(mg.getModel().getName())
					.append("</prefix></model>").append(NL);
		}
		generatPaths(model, out, WriteMode.XML.value);
		out.append("</modelPaths>");
		out.flush();
		out.close();
	}

	public void generateASCII(String url, String outFile) throws IOException {
		init(url);

		PrintStream out = new PrintStream(new File(outFile));
		generatPaths(model, out, WriteMode.ASCII.value);
		out.flush();
		out.close();
	}

	private void init(String modelURL) {
		vodml = new VODMLManager(new RemoteVODMLRegistry(), modelURL);
		graph = vodml.findModelForURL(modelURL);
		this.model = graph.getModel();

	}
}
