@echo off
echo ==========================================
echo 正在强制使用 Java 21 启动项目...
echo ==========================================

REM 1. 强制设置 JAVA_HOME 为你的 Dragonwell 21 路径
set JAVA_HOME=C:\Users\rrr\.jdks\dragonwell-ex-21.0.8

REM 2. 强制把 Java 21 放到系统路径的最前面 (覆盖掉 Java 24)
set PATH=%JAVA_HOME%\bin;%PATH%

REM 3. 验证一下现在的版本 (必须显示 21)
java -version

echo ==========================================
echo 开始运行 Spring Boot...
echo ==========================================

REM 4. 启动！
call mvnw clean spring-boot:run

pause