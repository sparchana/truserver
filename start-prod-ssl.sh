trap "{ echo Stopping truserver; sudo /home/ec2-user/truprojects/truserver/prod/activator-1.3.9-minimal/bin/activator stopProd; exit 0; }" EXIT
echo Starting truserver
sudo prod/activator-1.3.9-minimal/bin/activator "start -Dhttp.port=80 -Dconfig.resource=application-prod.conf -Dhttps.port=443 -Dhttps.keyStore=/home/ec2-user/.keystore/www_trujobs_in.jks -Dhttps.keyStorePassword=FerrarI234"
