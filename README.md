# 🚀 ShowcaseV2

An interactive rich-client web application showcasing arcade-style mini-games (Snake, Spaceforce) built using **Java** and **Google Web Toolkit (GWT)**, running on an **Apache Tomcat** application server.

---

## 🛠️ Prerequisites

To run this application locally, you will need:
*   **Java Development Kit (JDK)**: Version 8 or newer.
*   **IDE**: Eclipse (Eclipse IDE for Enterprise Java Developers recommended).
*   **GWT SDK**: Version 2.12.1 installed/linked.
*   **Application Server**: Apache Tomcat (v9.0+ recommended).

---

## 💻 Running the App Locally (Eclipse)

### 1. Import the Project
1. Open Eclipse and navigate to **File** ➡️ **Import...** ➡️ **General** ➡️ **Existing Projects into Workspace**.
2. Select the cloned repository root folder (`ShowcaseV2`).
3. Ensure `.project` and `.classpath` are detected, then click **Finish**.

### 2. Configure GWT Build Paths
1. Right-click the project ➡️ **Properties** ➡️ **Java Build Path** ➡️ **Libraries**.
2. Verify that the **GWT SDK 2.12.1** library is active. If missing, link your local GWT runtime library path.

### 3. Launch Development Mode
1. Right-click the project ➡️ **Run As** ➡️ **GWT Development Mode Application**.
2. Open the local link generated in the development console (typically `http://localhost:8888/Showcase.html`).

---

## 📦 Production Deployment (Tomcat)

You can deploy this application to a standalone Tomcat environment using one of two standard methods.

### Method A: Automated WAR Export (Recommended)
*If your Eclipse flavor includes Java EE / Web Tools Developer extensions:*
1. Right-click the project ➡️ **Export** ➡️ **Web** ➡️ **WAR file**.
2. Set the destination directory directly to your Tomcat server's **`webapps/`** folder.
3. Name the file `Showcase.war`. Tomcat will automatically unpack and deploy it on startup.

### Method B: Manual Assets Copy
*If your IDE setup lacks the enterprise export wizard:*
1. Click the **GWT Compile** button in your Eclipse toolbar to compile Java to static production JavaScript assets.
2. In your Tomcat server's **`webapps/`** directory, create a new folder named exactly `Showcase`.
3. Copy the entire contents of your local project's **`/war`** folder directly into that new Tomcat server folder. 
4. *Note: Ensure you only copy the runtime files (`WEB-INF`, `showcase`, `Showcase.html`, `Showcase.css`), skipping local temp caches like `gwt-unitCache`*.

### Accessing the Server Build
Start your Tomcat web service and navigate to:
```text
http://localhost:8080/Showcase/Showcase.html
```

---
