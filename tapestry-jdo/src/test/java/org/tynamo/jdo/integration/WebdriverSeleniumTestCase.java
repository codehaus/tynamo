/*
 * Copyright 2012 polrtex.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tynamo.jdo.integration;

/**
 *
 * @author polrtex
 */
// Copyright 2009, 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;
import java.io.File;
import java.lang.reflect.Method;
import org.apache.tapestry5.test.*;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.xml.XmlTest;

/**
 * Base class for creating Selenium-based integration test cases. This class
 * implements all the methods of {@link Selenium} and delegates to an instance
 * (setup once per test by
 * {@link #testStartup(String, String, int, int, String, ITestContext, XmlTest)}.
 *
 * @since 5.2.0
 */
public class WebdriverSeleniumTestCase extends Assert implements Selenium {

    /**
     * 15 seconds
     */
    public static final String PAGE_LOAD_TIMEOUT = "15000";
    private Selenium delegate;
    private String baseURL;
    private ErrorReporter errorReporter;
    private ITestContext testContext;

    /**
     * Starts up the servers for the entire test (i.e., for multiple TestCases).
     * By placing &lt;parameter&gt; elements inside the appropriate &lt;test&gt;
     * (of your testng.xml configuration file), you can change the configuration
     * or behavior of the servers. It is common to have two or more identical
     * tests that differ only in terms of the
     * <code>tapestry.browser-start-command</code> parameter, to run tests
     * against multiple browsers. <table> <tr> <th>Parameter</th> <th>Name</th>
     * <th>Default</th> <th>Description</th> </tr> <tr> <td>webAppFolder</td>
     * <td>tapestry.web-app-folder</td> <td>src/main/webapp</td> <td>Location of
     * web application context</td> </tr> <tr> <td>contextPath</td>
     * <td>tapestry.context-path</td> <td><em>empty string</em></td> <td>Context
     * path (defaults to root). As elsewhere, the context path should be blank,
     * or start with a slash (but not end with one).</td> </tr> <tr>
     * <td>port</td> <td>tapestry.port</td> <td>9090</td> <td>Port number for
     * web server to listen to</td> </tr> <tr> <td>sslPort</td>
     * <td>tapestry.ssl-port</td> <td>8443</td> <td>Port number for web server
     * to listen to for secure requests</td> </tr> <tr>
     * <td>browserStartCommand</td> <td>tapestry.browser-start-command</td>
     * <td>*firefox</td> <td>Command string used to launch the browser, as
     * defined by Selenium</td> </tr> </table> <p> Tests in the
     * <em>beforeStartup</em> group will be run before the start of Selenium.
     * This can be used to programmatically override the above parameter values.
     * For an example see
     * {@link org.apache.tapestry5.integration.reload.ReloadTests#beforeStartup}.
     * <p> This method will be invoked in <em>each</em> subclass, but is set up
     * to only startup the servers once (it checks the {@link ITestContext} to
     * see if the necessary keys are already present).
     *
     * @param testContext Used to share objects between the launcher and the
     * test suites
     * @throws Exception
     */
    @BeforeTest(dependsOnGroups = {"beforeStartup"})
    public void testStartup(final ITestContext testContext, XmlTest xmlTest) throws Exception {
        // This is not actually necessary, because TestNG will only invoke this method once
        // even when multiple test cases within the test extend from SeleniumTestCase. TestNG
        // just invokes it on the "first" TestCase instance it has test methods for.

        if (testContext.getAttribute(TapestryTestConstants.SHUTDOWN_ATTRIBUTE) != null) {
            return;
        }

        // If a parameter is overridden in another test method, TestNG won't pass the
        // updated value via a parameter, but still passes the original (coming from testng.xml or the default).
        // Seems like a TestNG bug.

        // Map<String, String> testParameters = xmlTest.getParameters();

        String webAppFolder = getParameter(xmlTest, TapestryTestConstants.WEB_APP_FOLDER_PARAMETER, "src/main/webapp");
        String contextPath = getParameter(xmlTest, TapestryTestConstants.CONTEXT_PATH_PARAMETER, "");
        int port = Integer.parseInt(getParameter(xmlTest, TapestryTestConstants.PORT_PARAMETER, "9090"));
        int sslPort = Integer.parseInt(getParameter(xmlTest, TapestryTestConstants.SSL_PORT_PARAMETER, "8443"));
        String browserStartCommand = getParameter(xmlTest, TapestryTestConstants.BROWSER_START_COMMAND_PARAMETER,
                "*firefox");

        final Runnable stopWebServer = launchWebServer(webAppFolder, contextPath, port, sslPort);

        final SeleniumServer seleniumServer = new SeleniumServer();

        File ffProfileTemplate = new File(TapestryTestConstants.MODULE_BASE_DIR, "src/test/conf/ff_profile_template");

        if (ffProfileTemplate.isDirectory()) {
            seleniumServer.getConfiguration().setFirefoxProfileTemplate(ffProfileTemplate);
        }

        seleniumServer.start();

        String baseURL = String.format("http://localhost:%d%s/", port, contextPath);

        CommandProcessor httpCommandProcessor = new HttpCommandProcessor("localhost", RemoteControlConfiguration.DEFAULT_PORT,
                browserStartCommand, baseURL);

        ErrorReporter errorReporter = new ErrorReporterImpl(httpCommandProcessor, testContext);

        ErrorReportingCommandProcessor commandProcessor = new ErrorReportingCommandProcessor(httpCommandProcessor, errorReporter);

        final Selenium selenium;
        if ("htmlunit".equals(browserStartCommand)) {
            HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
            
            driver.setJavascriptEnabled(true);
             
            selenium = new WebDriverBackedSelenium(driver,baseURL);
        } else {
            selenium = new DefaultSelenium(commandProcessor);
            selenium.start();
        }

        testContext.setAttribute(TapestryTestConstants.BASE_URL_ATTRIBUTE, baseURL);
        testContext.setAttribute(TapestryTestConstants.SELENIUM_ATTRIBUTE, selenium);
        testContext.setAttribute(TapestryTestConstants.ERROR_REPORTER_ATTRIBUTE, errorReporter);
        testContext.setAttribute(TapestryTestConstants.COMMAND_PROCESSOR_ATTRIBUTE, commandProcessor);

        testContext.setAttribute(TapestryTestConstants.SHUTDOWN_ATTRIBUTE, new Runnable() {

            public void run() {
                try {
                    selenium.stop();
                    seleniumServer.stop();
                    stopWebServer.run();
                } finally {

                    testContext.removeAttribute(TapestryTestConstants.BASE_URL_ATTRIBUTE);
                    testContext.removeAttribute(TapestryTestConstants.SELENIUM_ATTRIBUTE);
                    testContext.removeAttribute(TapestryTestConstants.ERROR_REPORTER_ATTRIBUTE);
                    testContext.removeAttribute(TapestryTestConstants.COMMAND_PROCESSOR_ATTRIBUTE);
                    testContext.removeAttribute(TapestryTestConstants.SHUTDOWN_ATTRIBUTE);
                }
            }
        });
    }

    private final String getParameter(XmlTest xmlTest, String key, String defaultValue) {
        String value = xmlTest.getParameter(key);

        return value != null ? value : defaultValue;
    }

    /**
     * Like {@link #testStartup(String, String, int, int, String, ITestContext, XmlTest)},
     * this may be called multiple times against multiple instances, but only
     * does work the first time.
     */
    @AfterTest
    public void testShutdown(ITestContext context) {
        // Likewise, this method should only be invoked once.
        Runnable r = (Runnable) context.getAttribute(TapestryTestConstants.SHUTDOWN_ATTRIBUTE);

        // This test is still useful, however, because testStartup() may not have completed properly,
        // and the runnable is the last thing it puts into the test context.

        if (r != null) {
            r.run();
        }
    }

    /**
     * Invoked from {@link #testStartup(String, String, int, String, ITestContext)}
     * to launch the web server to be tested. The return value is a Runnable
     * that will shut down the launched server at the end of the test (it is
     * coded this way so that the default Jetty web server can be more easily
     * replaced).
     *
     * @param webAppFolder path to the web application context
     * @param contextPath the path the context is mapped to, usually the empty
     * string
     * @param port the port number the server should handle
     * @param sslPort the port number on which the server should handle secure
     * requests
     * @return Runnable used to shut down the server
     * @throws Exception
     */
    protected Runnable launchWebServer(String webAppFolder, String contextPath, int port, int sslPort) throws Exception {
        final Jetty7Runner runner = new Jetty7Runner(webAppFolder, contextPath, port, sslPort);

        return new Runnable() {

            public void run() {
                runner.stop();
            }
        };
    }

    @BeforeClass
    public void setup(ITestContext context) {
        this.testContext = context;

        delegate = (Selenium) context.getAttribute(TapestryTestConstants.SELENIUM_ATTRIBUTE);
        baseURL = (String) context.getAttribute(TapestryTestConstants.BASE_URL_ATTRIBUTE);
        errorReporter = (ErrorReporter) context.getAttribute(TapestryTestConstants.ERROR_REPORTER_ATTRIBUTE);
    }

    @AfterClass
    public void cleanup() {
        delegate = null;
        baseURL = null;
        errorReporter = null;
        testContext = null;
    }

    /**
     * Delegates to {@link ErrorReporter#writeErrorReport()} to capture the
     * current page markup in a file for later analysis.
     */
    protected void writeErrorReport() {
        errorReporter.writeErrorReport();
    }

    /**
     * Returns the base URL for the application. This is of the typically
     * <code>http://localhost:9999/</code> (i.e., it includes a trailing slash).
     */
    public String getBaseURL() {
        return baseURL;
    }

    @BeforeMethod
    public void indicateTestMethodName(Method testMethod) {
        testContext.setAttribute(TapestryTestConstants.CURRENT_TEST_METHOD_ATTRIBUTE, testMethod);

        String className = testMethod.getDeclaringClass().getSimpleName();
        String testName = testMethod.getName().replace("_", " ");

        delegate.setContext(className + ": " + testName);
    }

    @AfterMethod
    public void cleanupTestMethod() {
        testContext.setAttribute(TapestryTestConstants.CURRENT_TEST_METHOD_ATTRIBUTE, null);
    }

    // ---------------------------------------------------------------------
    // Start of delegate methods
    //
    // When upgrading to a new version of Selenium, it is probably easiest
    // to delete all these methods and use the Generate Delegate Methods
    // refactoring.
    // ---------------------------------------------------------------------
    public void addCustomRequestHeader(String key, String value) {
        delegate.addCustomRequestHeader(key, value);
    }

    public void addLocationStrategy(String strategyName, String functionDefinition) {
        delegate.addLocationStrategy(strategyName, functionDefinition);
    }

    public void addScript(String scriptContent, String scriptTagId) {
        delegate.addScript(scriptContent, scriptTagId);
    }

    public void addSelection(String locator, String optionLocator) {
        delegate.addSelection(locator, optionLocator);
    }

    public void allowNativeXpath(String allow) {
        delegate.allowNativeXpath(allow);
    }

    public void altKeyDown() {
        delegate.altKeyDown();
    }

    public void altKeyUp() {
        delegate.altKeyUp();
    }

    public void answerOnNextPrompt(String answer) {
        delegate.answerOnNextPrompt(answer);
    }

    public void assignId(String locator, String identifier) {
        delegate.assignId(locator, identifier);
    }

    public void attachFile(String fieldLocator, String fileLocator) {
        delegate.attachFile(fieldLocator, fileLocator);
    }

    public void captureEntirePageScreenshot(String filename, String kwargs) {
        delegate.captureEntirePageScreenshot(filename, kwargs);
    }

    public String captureEntirePageScreenshotToString(String kwargs) {
        return delegate.captureEntirePageScreenshotToString(kwargs);
    }

    public String captureNetworkTraffic(String type) {
        return delegate.captureNetworkTraffic(type);
    }

    public void captureScreenshot(String filename) {
        delegate.captureScreenshot(filename);
    }

    public String captureScreenshotToString() {
        return delegate.captureScreenshotToString();
    }

    public void check(String locator) {
        delegate.check(locator);
    }

    public void chooseCancelOnNextConfirmation() {
        delegate.chooseCancelOnNextConfirmation();
    }

    public void chooseOkOnNextConfirmation() {
        delegate.chooseOkOnNextConfirmation();
    }

    public void click(String locator) {
        delegate.click(locator);
    }

    public void clickAt(String locator, String coordString) {
        delegate.clickAt(locator, coordString);
    }

    public void close() {
        delegate.close();
    }

    public void contextMenu(String locator) {
        delegate.contextMenu(locator);
    }

    public void contextMenuAt(String locator, String coordString) {
        delegate.contextMenuAt(locator, coordString);
    }

    public void controlKeyDown() {
        delegate.controlKeyDown();
    }

    public void controlKeyUp() {
        delegate.controlKeyUp();
    }

    public void createCookie(String nameValuePair, String optionsString) {
        delegate.createCookie(nameValuePair, optionsString);
    }

    public void deleteAllVisibleCookies() {
        delegate.deleteAllVisibleCookies();
    }

    public void deleteCookie(String name, String optionsString) {
        delegate.deleteCookie(name, optionsString);
    }

    public void deselectPopUp() {
        delegate.deselectPopUp();
    }

    public void doubleClick(String locator) {
        delegate.doubleClick(locator);
    }

    public void doubleClickAt(String locator, String coordString) {
        delegate.doubleClickAt(locator, coordString);
    }

    public void dragAndDrop(String locator, String movementsString) {
        delegate.dragAndDrop(locator, movementsString);
    }

    public void dragAndDropToObject(String locatorOfObjectToBeDragged, String locatorOfDragDestinationObject) {
        delegate.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
    }

    public void dragdrop(String locator, String movementsString) {
        delegate.dragdrop(locator, movementsString);
    }

    public void fireEvent(String locator, String eventName) {
        delegate.fireEvent(locator, eventName);
    }

    public void focus(String locator) {
        delegate.focus(locator);
    }

    public String getAlert() {
        return delegate.getAlert();
    }

    public String[] getAllButtons() {
        return delegate.getAllButtons();
    }

    public String[] getAllFields() {
        return delegate.getAllFields();
    }

    public String[] getAllLinks() {
        return delegate.getAllLinks();
    }

    public String[] getAllWindowIds() {
        return delegate.getAllWindowIds();
    }

    public String[] getAllWindowNames() {
        return delegate.getAllWindowNames();
    }

    public String[] getAllWindowTitles() {
        return delegate.getAllWindowTitles();
    }

    public String getAttribute(String attributeLocator) {
        return delegate.getAttribute(attributeLocator);
    }

    public String[] getAttributeFromAllWindows(String attributeName) {
        return delegate.getAttributeFromAllWindows(attributeName);
    }

    public String getBodyText() {
        return delegate.getBodyText();
    }

    public String getConfirmation() {
        return delegate.getConfirmation();
    }

    public String getCookie() {
        return delegate.getCookie();
    }

    public String getCookieByName(String name) {
        return delegate.getCookieByName(name);
    }

    public Number getCursorPosition(String locator) {
        return delegate.getCursorPosition(locator);
    }

    public Number getElementHeight(String locator) {
        return delegate.getElementHeight(locator);
    }

    public Number getElementIndex(String locator) {
        return delegate.getElementIndex(locator);
    }

    public Number getElementPositionLeft(String locator) {
        return delegate.getElementPositionLeft(locator);
    }

    public Number getElementPositionTop(String locator) {
        return delegate.getElementPositionTop(locator);
    }

    public Number getElementWidth(String locator) {
        return delegate.getElementWidth(locator);
    }

    public String getEval(String script) {
        return delegate.getEval(script);
    }

    public String getExpression(String expression) {
        return delegate.getExpression(expression);
    }

    public String getHtmlSource() {
        return delegate.getHtmlSource();
    }

    public String getLocation() {
        return delegate.getLocation();
    }

    public String getLog() {
        return delegate.getLog();
    }

    public Number getMouseSpeed() {
        return delegate.getMouseSpeed();
    }

    public String getPrompt() {
        return delegate.getPrompt();
    }

    public String getSelectedId(String selectLocator) {
        return delegate.getSelectedId(selectLocator);
    }

    public String[] getSelectedIds(String selectLocator) {
        return delegate.getSelectedIds(selectLocator);
    }

    public String getSelectedIndex(String selectLocator) {
        return delegate.getSelectedIndex(selectLocator);
    }

    public String[] getSelectedIndexes(String selectLocator) {
        return delegate.getSelectedIndexes(selectLocator);
    }

    public String getSelectedLabel(String selectLocator) {
        return delegate.getSelectedLabel(selectLocator);
    }

    public String[] getSelectedLabels(String selectLocator) {
        return delegate.getSelectedLabels(selectLocator);
    }

    public String getSelectedValue(String selectLocator) {
        return delegate.getSelectedValue(selectLocator);
    }

    public String[] getSelectedValues(String selectLocator) {
        return delegate.getSelectedValues(selectLocator);
    }

    public String[] getSelectOptions(String selectLocator) {
        return delegate.getSelectOptions(selectLocator);
    }

    public String getSpeed() {
        return delegate.getSpeed();
    }

    public String getTable(String tableCellAddress) {
        return delegate.getTable(tableCellAddress);
    }

    public String getText(String locator) {
        return delegate.getText(locator);
    }

    public String getTitle() {
        return delegate.getTitle();
    }

    public String getValue(String locator) {
        return delegate.getValue(locator);
    }

    public boolean getWhetherThisFrameMatchFrameExpression(String currentFrameString, String target) {
        return delegate.getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
    }

    public boolean getWhetherThisWindowMatchWindowExpression(String currentWindowString, String target) {
        return delegate.getWhetherThisWindowMatchWindowExpression(currentWindowString, target);
    }

    public Number getXpathCount(String xpath) {
        return delegate.getXpathCount(xpath);
    }

    public void goBack() {
        delegate.goBack();
    }

    public void highlight(String locator) {
        delegate.highlight(locator);
    }

    public void ignoreAttributesWithoutValue(String ignore) {
        delegate.ignoreAttributesWithoutValue(ignore);
    }

    public boolean isAlertPresent() {
        return delegate.isAlertPresent();
    }

    public boolean isChecked(String locator) {
        return delegate.isChecked(locator);
    }

    public boolean isConfirmationPresent() {
        return delegate.isConfirmationPresent();
    }

    public boolean isCookiePresent(String name) {
        return delegate.isCookiePresent(name);
    }

    public boolean isEditable(String locator) {
        return delegate.isEditable(locator);
    }

    public boolean isElementPresent(String locator) {
        return delegate.isElementPresent(locator);
    }

    public boolean isOrdered(String locator1, String locator2) {
        return delegate.isOrdered(locator1, locator2);
    }

    public boolean isPromptPresent() {
        return delegate.isPromptPresent();
    }

    public boolean isSomethingSelected(String selectLocator) {
        return delegate.isSomethingSelected(selectLocator);
    }

    public boolean isTextPresent(String pattern) {
        return delegate.isTextPresent(pattern);
    }

    public boolean isVisible(String locator) {
        return delegate.isVisible(locator);
    }

    public void keyDown(String locator, String keySequence) {
        delegate.keyDown(locator, keySequence);
    }

    public void keyDownNative(String keycode) {
        delegate.keyDownNative(keycode);
    }

    public void keyPress(String locator, String keySequence) {
        delegate.keyPress(locator, keySequence);
    }

    public void keyPressNative(String keycode) {
        delegate.keyPressNative(keycode);
    }

    public void keyUp(String locator, String keySequence) {
        delegate.keyUp(locator, keySequence);
    }

    public void keyUpNative(String keycode) {
        delegate.keyUpNative(keycode);
    }

    public void metaKeyDown() {
        delegate.metaKeyDown();
    }

    public void metaKeyUp() {
        delegate.metaKeyUp();
    }

    public void mouseDown(String locator) {
        delegate.mouseDown(locator);
    }

    public void mouseDownAt(String locator, String coordString) {
        delegate.mouseDownAt(locator, coordString);
    }

    public void mouseDownRight(String locator) {
        delegate.mouseDownRight(locator);
    }

    public void mouseDownRightAt(String locator, String coordString) {
        delegate.mouseDownRightAt(locator, coordString);
    }

    public void mouseMove(String locator) {
        delegate.mouseMove(locator);
    }

    public void mouseMoveAt(String locator, String coordString) {
        delegate.mouseMoveAt(locator, coordString);
    }

    public void mouseOut(String locator) {
        delegate.mouseOut(locator);
    }

    public void mouseOver(String locator) {
        delegate.mouseOver(locator);
    }

    public void mouseUp(String locator) {
        delegate.mouseUp(locator);
    }

    public void mouseUpAt(String locator, String coordString) {
        delegate.mouseUpAt(locator, coordString);
    }

    public void mouseUpRight(String locator) {
        delegate.mouseUpRight(locator);
    }

    public void mouseUpRightAt(String locator, String coordString) {
        delegate.mouseUpRightAt(locator, coordString);
    }

    public void open(String url) {
        delegate.open(url);
    }

    public void open(String url, String ignoreResponseCode) {
        delegate.open(url, ignoreResponseCode);
    }

    public void openWindow(String url, String windowID) {
        delegate.openWindow(url, windowID);
    }

    public void refresh() {
        delegate.refresh();
    }

    public void removeAllSelections(String locator) {
        delegate.removeAllSelections(locator);
    }

    public void removeScript(String scriptTagId) {
        delegate.removeScript(scriptTagId);
    }

    public void removeSelection(String locator, String optionLocator) {
        delegate.removeSelection(locator, optionLocator);
    }

    public String retrieveLastRemoteControlLogs() {
        return delegate.retrieveLastRemoteControlLogs();
    }

    public void rollup(String rollupName, String kwargs) {
        delegate.rollup(rollupName, kwargs);
    }

    public void runScript(String script) {
        delegate.runScript(script);
    }

    public void select(String selectLocator, String optionLocator) {
        delegate.select(selectLocator, optionLocator);
    }

    public void selectFrame(String locator) {
        delegate.selectFrame(locator);
    }

    public void selectPopUp(String windowID) {
        delegate.selectPopUp(windowID);
    }

    public void selectWindow(String windowID) {
        delegate.selectWindow(windowID);
    }

    public void setBrowserLogLevel(String logLevel) {
        delegate.setBrowserLogLevel(logLevel);
    }

    public void setContext(String context) {
        delegate.setContext(context);
    }

    public void setCursorPosition(String locator, String position) {
        delegate.setCursorPosition(locator, position);
    }

    public void setExtensionJs(String extensionJs) {
        delegate.setExtensionJs(extensionJs);
    }

    public void setMouseSpeed(String pixels) {
        delegate.setMouseSpeed(pixels);
    }

    public void setSpeed(String value) {
        delegate.setSpeed(value);
    }

    public void setTimeout(String timeout) {
        delegate.setTimeout(timeout);
    }

    public void shiftKeyDown() {
        delegate.shiftKeyDown();
    }

    public void shiftKeyUp() {
        delegate.shiftKeyUp();
    }

    public void showContextualBanner() {
        delegate.showContextualBanner();
    }

    public void showContextualBanner(String className, String methodName) {
        delegate.showContextualBanner(className, methodName);
    }

    public void shutDownSeleniumServer() {
        delegate.shutDownSeleniumServer();
    }

    public void start() {
        delegate.start();
    }

    public void start(Object optionsObject) {
        delegate.start(optionsObject);
    }

    public void start(String optionsString) {
        delegate.start(optionsString);
    }

    public void stop() {
        delegate.stop();
    }

    public void submit(String formLocator) {
        delegate.submit(formLocator);
    }

    public void type(String locator, String value) {
        delegate.type(locator, value);
    }

    public void typeKeys(String locator, String value) {
        delegate.typeKeys(locator, value);
    }

    public void uncheck(String locator) {
        delegate.uncheck(locator);
    }

    public void useXpathLibrary(String libraryName) {
        delegate.useXpathLibrary(libraryName);
    }

    public void waitForCondition(String script, String timeout) {
        delegate.waitForCondition(script, timeout);
    }

    public void waitForFrameToLoad(String frameAddress, String timeout) {
        delegate.waitForFrameToLoad(frameAddress, timeout);
    }

    public void waitForPageToLoad(String timeout) {
        delegate.waitForPageToLoad(timeout);
    }

    public void waitForPopUp(String windowID, String timeout) {
        delegate.waitForPopUp(windowID, timeout);
    }

    public void windowFocus() {
        delegate.windowFocus();
    }

    public void windowMaximize() {
        delegate.windowMaximize();
    }

    // ---------------------------------------------------------------------
    // End of delegate methods
    // ---------------------------------------------------------------------
    protected final void unreachable() {
        writeErrorReport();

        throw new AssertionError("This statement should not be reachable.");
    }

    protected final void openBaseURL() {
        open(baseURL);
    }

    /**
     * Asserts the text of an element, identified by the locator.
     *
     * @param locator identifies the element whose text value is to be asserted
     * @param expected expected value for the element's text
     */
    protected final void assertText(String locator, String expected) {
        String actual = null;

        try {
            actual = getText(locator);
        } catch (RuntimeException ex) {
            System.err.printf("Error accessing %s: %s, in:\n\n%s\n\n", locator, ex.getMessage(), getHtmlSource());

            throw ex;
        }

        if (actual.equals(expected)) {
            return;
        }

        writeErrorReport();

        throw new AssertionError(String.format("%s was '%s' not '%s'", locator, actual, expected));
    }

    protected final void assertTextPresent(String... text) {
        for (String item : text) {
            if (isTextPresent(item)) {
                continue;
            }

            writeErrorReport();

            throw new AssertionError("Page did not contain '" + item + "'.");
        }
    }

    /**
     * Assets that each string provided is present somewhere in the current
     * document.
     *
     * @param expected string expected to be present
     */
    protected final void assertSourcePresent(String... expected) {
        String source = getHtmlSource();

        for (String snippet : expected) {
            if (source.contains(snippet)) {
                continue;
            }

            writeErrorReport();

            throw new AssertionError("Page did not contain source '" + snippet + "'.");
        }
    }

    /**
     * Click a link identified by a locator, then wait for the resulting page to
     * load. This is not useful for Ajax updates, just normal full-page
     * refreshes.
     *
     * @param locator identifies the link to click
     */
    protected final void clickAndWait(String locator) {
        click(locator);

        waitForPageToLoad();
    }

    /**
     * Waits for the page to load (up to 15 seconds). This is invoked after
     * clicking on an element that forces a full page refresh.
     */
    protected final void waitForPageToLoad() {
        waitForPageToLoad(PAGE_LOAD_TIMEOUT);
    }

    /**
     * Used when the locator identifies an attribute, not an element.
     *
     * @param locator identifies the attribute whose value is to be asserted
     * @param expected expected value for the attribute
     */
    protected final void assertAttribute(String locator, String expected) {
        String actual = null;

        try {
            actual = getAttribute(locator);
        } catch (RuntimeException ex) {
            System.err.printf("Error accessing %s: %s", locator, ex.getMessage());

            writeErrorReport();

            throw ex;
        }

        if (actual.equals(expected)) {
            return;
        }

        writeErrorReport();

        throw new AssertionError(String.format("%s was '%s' not '%s'", locator, actual, expected));
    }

    protected final void assertFieldValue(String locator, String expected) {
        try {
            assertEquals(getValue(locator), expected);
        } catch (AssertionError ex) {
            writeErrorReport();

            throw ex;
        }
    }

    public Number getCssCount(String string) {
        return delegate.getCssCount(string);
    }
}
