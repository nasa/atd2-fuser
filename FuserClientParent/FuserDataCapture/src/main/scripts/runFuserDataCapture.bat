
@title FUSERDATACAPTURE

@set LOG_DIR=logs
@IF DEFINED OUTFILE_DIR SET LOG_DIR=%OUTFILE_DIR%
@set LOGFILE=%LOG_DIR%\fuser-data-capture.log
@SET LOGCONFIG=-Dlog4j.configuration=file:config/fuser-data-capture/log4j.xml -Dlogfile.name=%LOGFILE%

@IF NOT EXIST %LOG_DIR% MKDIR %LOG_DIR%

@set CPATH=.;./env;./config;./lib/*

java -server -Xmx1024m -Dapp.name=FuserDataCapture -Duser.timezone=UTC -classpath %CPATH% %LOGCONFIG% com.mosaicatm.fuser.datacapture.FuserDataCaptureMain %*
