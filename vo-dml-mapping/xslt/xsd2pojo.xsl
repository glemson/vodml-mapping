<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
<!ENTITY text "<xsl:text></xsl:text>">
<!ENTITY cr "<xsl:text>
</xsl:text>">
<!ENTITY bl "<xsl:text> </xsl:text>">
<!ENTITY tab "<xsl:text>  </xsl:text>">
<!ENTITY tab2 "<xsl:text>    </xsl:text>">
<!ENTITY tab3 "<xsl:text>      </xsl:text>">
<!ENTITY tab4 "<xsl:text>        </xsl:text>">
<!ENTITY sc "<xsl:text>;
</xsl:text>">
]>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            		xmlns:exsl="http://exslt.org/common"
                xmlns:map="http://volute.g-vo.org/dm/vo-dml-mapping/v0.9"
                extension-element-prefixes="exsl">

<!-- 
  This XSLT script transforms a data model in VO-DML/XML representation to 
  Purely Ordinary Java Classes.
  
  Only defines fields for components.
  Only argumentless constructor.
  
  Java 1.5+ is required by these two libraries.
-->

  <xsl:output method="text" encoding="UTF-8" indent="yes" />

  <xsl:param name="dir"/>
  <xsl:param name="package"/>
  

  <xsl:strip-space elements="*" />
  
  <!-- next could be parameters -->

  <!-- main pattern : processes for root node model -->
  <xsl:template match="/">
  <xsl:apply-templates select="*:schema"/>
</xsl:template>

  <!-- model pattern : generates gen-log and processes nodes package and generates the ModelVersion class and persistence.xml -->
  <xsl:template match="*:schema">
    <xsl:apply-templates select="*:complexType" mode="file"/>
  </xsl:template>  

  <xsl:template match="*:complexType" mode="file">

    <xsl:variable name="file" select="concat($dir, '/', @name, '.java')"/>

    <!-- open file for this class -->
<!--  
    <xsl:message >Opening file <xsl:value-of select="$file"/></xsl:message>
-->      
    <xsl:result-document href="{$file}">
      <xsl:apply-templates select="." mode="class"/>
    </xsl:result-document>
  </xsl:template>


  <!-- template class creates a java class (JPA compliant) for UML object & data types -->
  <xsl:template match="*:complexType" mode="class">
  <xsl:variable name="name">
    <xsl:call-template name="JavaType">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
package <xsl:value-of select="$package"/>;

import org.ivoa.vodml.xsd.*;

<xsl:if test=".//*:element[@maxOccurs='unbounded']">
import java.util.ArrayList;
 </xsl:if>
public&bl;<xsl:if test="@abstract='true'">abstract&bl;</xsl:if>class <xsl:value-of select="$name"/>&bl;
    <xsl:choose>
      <xsl:when test="*:complexContent/*:extension">extends&bl;<xsl:value-of select="*:complexContent/*:extension/@base"/></xsl:when>
      <xsl:otherwise>extends&bl;XMLElement</xsl:otherwise>
    </xsl:choose>
{
<xsl:apply-templates select=".//*:element" mode="staticfields"/>
<xsl:apply-templates select=".//*:attribute" mode="staticfields"/>
<xsl:apply-templates select=".//*:element" mode="fields"/>
<xsl:apply-templates select=".//*:attribute" mode="fields"/>

&tab;public <xsl:value-of select="@name"/>(XMLElement _parent) throws XMLParsingException {
&tab;&tab;super(_parent);
  }
<xsl:apply-templates select=".//*:element" mode="methods"/>
<xsl:apply-templates select=".//*:attribute" mode="methods"/>
<!--  
<xsl:apply-templates select=".//*:element" mode="generic"/>
-->
<xsl:apply-templates select="." mode="generic"/>

 /**    Put all hand modifications below this line */
 
}
</xsl:template>

<xsl:template match="*:element" mode="staticfields">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
&tab;public static final String E<xsl:value-of select="$name"/>="<xsl:value-of select="@name"/>"&sc;
</xsl:template>

<xsl:template match="*:attribute" mode="staticfields">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
&tab;public static final String A<xsl:value-of select="$name"/>="<xsl:value-of select="@name"/>"&sc;
</xsl:template>

<xsl:template match="*:element" mode="fields">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
&tab;private <xsl:apply-templates select="." mode="fieldtype"/>&bl;<xsl:value-of select="$name"/>&sc;
</xsl:template>

<xsl:template match="*:attribute" mode="fields">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="type">
  <xsl:call-template name="JavaType">
    <xsl:with-param name="name" select="@type"/>
  </xsl:call-template>
  </xsl:variable>
&tab;private <xsl:value-of select="$type"/>&bl;<xsl:value-of select="$name"/> &sc;
</xsl:template>

<xsl:template match="*:element" mode="fieldtype">
<!--  TODO deal with type defined in place, i.e. without a @type attribute -->
<xsl:variable name="type" >
  <xsl:call-template name="JavaElementType">
    <xsl:with-param name="name" select="@type"/>
  </xsl:call-template>
</xsl:variable>
<xsl:choose>
  <xsl:when test="./@maxOccurs='unbounded'">
<xsl:text>ArrayList</xsl:text>&lt;<xsl:value-of select="$type"/>&gt;&text;
  </xsl:when>
  <xsl:otherwise>
&text;<xsl:value-of select="$type"/>&text;
  </xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="*:element" mode="methods">
<xsl:variable name="name">
  <xsl:call-template name="JavaName">
    <xsl:with-param name="name" select="@name"/>
  </xsl:call-template>
</xsl:variable>
<xsl:variable name="type">
  <xsl:call-template name="JavaElementType">
    <xsl:with-param name="name" select="@type"/>
  </xsl:call-template>
</xsl:variable>
<xsl:choose>
<xsl:when test="@maxOccurs = 'unbounded'">
&tab;<xsl:value-of select="concat('public ',$type,' add',$name,'() throws XMLParsingException {')"/>&cr;
&tab2;if(<xsl:value-of select="$name"/> == null)<xsl:value-of select="$name"/> = new ArrayList&lt;<xsl:value-of select="$type"/>&gt;();
&tab2;<xsl:value-of select="concat($type,' el = new ',$type,'(this);')"/>&cr;
&tab2;<xsl:value-of select="concat($name,'.add(el);')"/>
&tab2;return el;
&tab;}
&tab;<xsl:value-of select="concat('public ArrayList&lt;',$type,'&gt; get',$name,'() {')"/>&cr;
&tab2;return this.<xsl:value-of select="$name"/>;
&tab;}
</xsl:when>
<xsl:otherwise>
&tab;<xsl:value-of select="concat('public ',$type,' add',$name,'() throws XMLParsingException {')"/>&cr;
&tab2;<xsl:value-of select="concat($type,' el = new ',$type,'(this);')"/>&cr;
&tab2;<xsl:value-of select="concat('this.',$name,' = el;')"/>&cr;
&tab2;<xsl:value-of select="concat('return this.',$name)"/>;&cr;
&tab;}
&tab;<xsl:value-of select="concat('public ',$type,' get',$name,'() {')"/>&cr;
&tab2;return this.<xsl:value-of select="$name"/>;
&tab;}
<xsl:if test="$type = 'XMLTextElement'">
&tab;<xsl:value-of select="concat('public String get',$name,'_value() {')"/>&cr;
&tab2;return (this.<xsl:value-of select="$name"/> == null?null:this.<xsl:value-of select="$name"/>.getValue());
&tab;}
</xsl:if>
</xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="*:attribute" mode="methods">
<xsl:variable name="name">
  <xsl:call-template name="JavaName">
    <xsl:with-param name="name" select="@name"/>
  </xsl:call-template>
</xsl:variable>
<xsl:variable name="type">
  <xsl:call-template name="JavaType">
    <xsl:with-param name="name" select="@type"/>
  </xsl:call-template>
</xsl:variable>
&tab;<xsl:value-of select="concat('public void set',$name,'(',$type,' _v) {')"/>&cr;
&tab2;<xsl:value-of select="concat('this.',$name,' = _v;')"/>&cr;
&tab;}&cr;
&tab;<xsl:value-of select="concat('public ',$type,' get',$name,'(){')"/>&cr;
&tab2;<xsl:value-of select="concat(' return this.',$name,';')"/>&cr;
&tab;}
</xsl:template>

<xsl:template match="*:complexType" mode="generic">
<xsl:if test="./*:attribute">
&tab;@Override&cr;
&tab;public void addAttribute(String name, String value) throws XMLIllegalAttributeException {&cr;
<xsl:for-each select="./*:attribute">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="./@name"/>
    </xsl:call-template>
  </xsl:variable>
&tab2;<xsl:if test="position()>1">else </xsl:if><xsl:value-of select="concat('if (A',$name,'.equals(name)) set',$name,'(value);')"/>&cr;
</xsl:for-each>
&tab2;else super.addAttribute(name, value);&cr;
&tab;}
</xsl:if>
<xsl:if test=".//*:element">
&tab;@Override&cr;
&tab;	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
<xsl:for-each select=".//*:element">
  <xsl:variable name="name">
    <xsl:call-template name="JavaName">
      <xsl:with-param name="name" select="./@name"/>
    </xsl:call-template>
  </xsl:variable>
&tab2;<xsl:if test="position()>1">else </xsl:if><xsl:value-of select="concat('if (E',$name,'.equals(name)) return add',$name,'();')"/>&cr;
</xsl:for-each>
&tab2;else return super.addElement(name, xsiType);&cr;
&tab;}

</xsl:if>

</xsl:template>
<!-- ==================================================== -->

<xsl:template name="JavaName">
  <xsl:param name="name"/>
<xsl:text>_</xsl:text><xsl:value-of select="replace($name,'-','_')"/>&text; 
</xsl:template>

<xsl:template name="JavaType">
  <xsl:param name="name"/>
<xsl:choose>
  <xsl:when test="starts-with($name,'xsd:') or starts-with($name,'xs:')">
  <xsl:call-template name="JavaFromXSDType">
  <xsl:with-param name="name" select="substring-after($name,':')"/>
  </xsl:call-template>
  </xsl:when>
 <xsl:otherwise>
&text;<xsl:value-of select="$name"/>&text;
  </xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template name="asSimpleType">
  <xsl:param name="name"/>
<xsl:if test="/*:schema/*:simpleType[@name=$name and not(./*:attribute)]">
<xsl:call-template name="JavaElementType">
<xsl:with-param name="name" select="/*:schema/*:simpleType[@name=$name]/*:restriction/@base"/>
</xsl:call-template>
</xsl:if>
</xsl:template>

<xsl:template name="JavaElementType">
  <xsl:param name="name"/>
<xsl:choose>
  <xsl:when test="starts-with($name,'xsd:') or starts-with($name,'xs:')">
<xsl:text>XMLTextElement</xsl:text>
  </xsl:when>
 <xsl:otherwise>
   <xsl:variable name="simpleType">
   <xsl:call-template name="asSimpleType">
     <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
   </xsl:variable>
 <xsl:choose>
   <xsl:when test="$simpleType != ''">
&text;<xsl:value-of select="$simpleType"/>&text;
</xsl:when>
<xsl:otherwise> 
&text;<xsl:value-of select="$name"/>&text;
  </xsl:otherwise>
 </xsl:choose>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template name="JavaFromXSDType">
  <xsl:param name="name"/>
<xsl:choose>
  <xsl:when test="$name = 'string'">
&text;String&text;
  </xsl:when>
  <xsl:when test="$name = 'anyURI'">
&text;String&text;
  </xsl:when>
 <xsl:otherwise>
&text;String&text;  
  </xsl:otherwise>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
