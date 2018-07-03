Command to start NODE on HUB
java -Dwebdriver.chrome.driver=C:\binaries\chromedriver.exe -Dwebdriver.ie.driver=C:\binaries\IEDriverServer.exe -Dwebdriver.gecko.driver=C:\binaries\geckodriver.exe -Dwebdriver.edge.driver=C:\binaries\MicrosoftWebDriver.exe -jar C:\binaries\selenium-server-standalone-3.4.0.jar -maxSession 2 -timeout 300 -browserTimeout 120 -role node -hub http://10.100.8.226:4444/grid/register

Command to start HUB
java -jar C:\binaries\selenium-server-standalone-3.4.0.jar -role hub

Jenkins Admin user:
artem
password1
matunart@gmail.com