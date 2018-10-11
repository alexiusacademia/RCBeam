# RCBeam Library

A java library for the analysis of reinforced concrete beams of any section.

### Section Features
- Hollow Sections
- Any shape (provided with `x,y` coordinates) except rounded
- Default unit is metric

### Limitations
- Horizontal line intersection in a polygon must not exceed 2.

### Analysis Results

- Moment
- Curvature
- Height of compression block

### Creating Steel in Tension Object

```java
// Creating object
SteelTension st = new SteelTension();

// Asigning the steel area, 2nd parameter is true if in metric units (in square millimeters)
st.setTotalArea(1200, Unit.METRIC);
```

### Creating Steel in Compression Object

```java
// Creating object
SteelCompression sc = new SteelCompression();

// Asigning the steel area, 2nd parameter is true if in metric units (square millimeters)
sc.setTotalArea(1200, Unit.METRIC);

// Set the location of compression steel reinf. from extrem concrete compression fiber (in millimeters)
sc.setdPrime(50, Unit.METRIC);
```

### Creating BeamSection Object

```java
// Creating object
BeamSection beam = new BeamSection();

// Create section shape
Section section = new Section();
List<Node> mainSection = new ArrayList();
mainSection.add(new BeamSectionNode(0, 0));
mainSection.add(new BeamSectionNode(0, 500));
mainSection.add(new BeamSectionNode(300, 500));
mainSection.add(new BeamSectionNode(300, 0));

section.setMainSection(mainSection);

beam.setSection(section);

// Define unit
beam.setUnit(Unit.METRIC);

// Define properties
beam.setFcPrime(21);
beam.setEffectiveDepth(450);
beam.setFy(275);

// Define reinforcements
beam.setSteelTension(st);
beam.setSteelCompression(sc);
```

### Creating a hollow section
Hollow portions are needed to define a hollow sections or sections that may have more than
 2 intersections if you draw a horizontal line at any point.
 For example, if you want to draw a section above with a v-shape cut at the top,
 you could use the following snippet as a guide:
```java
List<Node> clipping = new ArrayList();
clipping.add(new Node(100, 500));
clipping.add(new Node(200, 500));
clipping.add(new Node(150, 350));
clipping.add(new Node(100, 500));

section.addClipping(clipping);

```

### Creating an Analysis

```java
// Creating an object, specifying the beam section to be analyzed
BeamAnalyses analyses = new BeamAnalyses(bs);

// Defining a result from the analysis
BeamAnalysisResult uncrackAnalysis = analyses.uncrackedAnalysis();

// Accessing the result
// Cracking moment
System.out.println(uncrackAnalysis.getMomentC());
// Curvature right before cracking
System.out.println(uncrackAnalysis.getCurvatureC());
// Minimum tensile reinforcement as required by ACI code
System.out.println(uncrackAnalysis.getMinimumSteelTensionArea());
```

### Balanced Design Analysis 
```java
analyses.balancedAnalysis(StressDistribution.WHITNEY);
double Asb = analyses.getBalancedSteelTension();
```

### Nominal Moment Capacity
```java
BeamAnalysisResult limitAnalysis = analyses.beamCapacityAnalysis(StressDistribution.WHITNEY);
double Mn = limitAnalysis.getMomentC();
```



### How to Contribute

Anyone is welcome to contribute via coding or anything. For coding contributions, please refer to the issues and find the topic that best suits you.
If for a reason the topic you want to contribute isn't listed yet, feel free to create the issue and I'll try to respond as quickly as I could.