cd ConfigService
C:\Development\maven\bin\mvn -Dmaven.test.skip=true package
build_image
cd ..
cd DiscoveryService
C:\Development\maven\bin\mvn -Dmaven.test.skip=true package
build_image
cd ..
cd CourseService
C:\Development\maven\bin\mvn -Dmaven.test.skip=true package
build_image
cd ..
cd StudentService
C:\Development\maven\bin\mvn -Dmaven.test.skip=true package
build_image
cd ..
cd GatewayService
C:\Development\maven\bin\mvn -Dmaven.test.skip=true package
build_image
cd ..
