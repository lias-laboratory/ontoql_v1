dropdb -U postgres OntoQLJUnitTestAnnotation
createdb -U postgres OntoQLJUnitTestAnnotation
psql -U postgres OntoQLJUnitTestAnnotation < OntoQLJUnitTestAnnotation.dmp

dropdb -U postgres OntoQLJUnitTestMain
createdb -U postgres OntoQLJUnitTestMain
psql -U postgres OntoQLJUnitTestMain < OntoQLJUnitTestMain.dmp

dropdb -U postgres OntoQLJUnitTestMonoLingual
createdb -U postgres OntoQLJUnitTestMonoLingual
psql -U postgres OntoQLJUnitTestMonoLingual < OntoQLJUnitTestMonoLingual.dmp

dropdb -U postgres OntoQLJUnitTestPreference
createdb -U postgres OntoQLJUnitTestPreference
psql -U postgres OntoQLJUnitTestPreference < OntoQLJUnitTestPreference.dmp
