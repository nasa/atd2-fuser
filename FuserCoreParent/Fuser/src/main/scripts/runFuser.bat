
@title FuserMain

@set LOG_DIR=log
@IF DEFINED OUTFILE_DIR SET LOG_DIR=%OUTFILE_DIR%
@set LOGFILE=%LOG_DIR%\fuser.log
@SET LOGCONFIG=-Dlog4j.configuration=file:config/fuser/log4j.xml -Dlogfile.name=%LOGFILE%

@IF NOT EXIST %LOG_DIR% MKDIR %LOG_DIR%

@set CPATH=.;./env;./config;./lib/*

java -server -Xmx1024m -Dapp.name=Fuser -Duser.timezone=UTC -classpath %CPATH% %LOGCONFIG% com.mosaicatm.fuser.FuserMain %* > Fuser.out 2>&1