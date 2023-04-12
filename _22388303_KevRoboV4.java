
package KevRoboV3;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.Event;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.BulletMissedEvent;
import robocode.util.Utils;
import robocode.BulletHitEvent;

public class _22388303_KevRoboV4 extends AdvancedRobot {

  int moveCount, moveDirection = 1,moveMode =1, radarDirection = 1, count = 0;;
  Opponent enemy = new Opponent();
  double absoluteBearing, gunBearing, gunTurn, radarBearing;

  public void run() {
    //sepeate gun,radar and movement
    setAdjustRadarForGunTurn(true);
    setAdjustGunForRobotTurn(true);

    //reset enemy
    enemy.reset();
    //startradar
    setTurnRadarRight(360);
    while (true) {
    
      radarScan();
      movementStrategy();
      Fire();
	  
      execute();

    }
  }
  
 

  void Fire() {
//find bearings
    initBearing();

    if (Math.abs(gunBearing) <= 4) {
     //get the distance between the guns heading and the enemys location
      setTurnGunRight(getHeading() - getGunHeading() + enemy.getBearing());
	  //turn Radar to enemy
      setTurnRadarRight(radarBearing); 
	  //turn to enemy and correct a bit
      setTurnRight(containBearing(enemy.getBearing() + 80)); 
	  
 //wait till gun  is reset and Energy is ready
      if (getGunHeat() == 0 && getEnergy() > .2) {
    //fire equations
	
        fire(Math.min(4.5 - Math.abs(gunBearing) / 2 - enemy.getDistance() / 250, getEnergy() - .1));

      }
    }

    // otherwise just set the gun to turn.
    else {
      setTurnGunRight(gunBearing);
      setTurnRadarRight(radarBearing);
    }
  }

  void radarScan() {
    //find me a enemy
    if (enemy.none()) {
     setTurnRadarRight(360);
    }
    //focus enemy
    else {
	 //creeates a 30 degree cone in the direction of the enemies
      double radarCone = getHeading() - getRadarHeading() + enemy.getBearing();
	  //cone
      radarCone += 30 * radarDirection;
	  //set radar to enemy
      setTurnRadarRight(containBearing(radarCone));
      radarDirection *= -1;
    }

  }
  

	

	
 public void movementStrategy() {

//choses the movement strategy
if(moveMode==1){
     //square of agains enemy turned slighty so circle circle
		setTurnRight(containBearing(enemy.getBearing() + 90 - (20 * moveDirection)));
		//change dir on stop
		if (getVelocity() == 0) {
			moveDirection *= -1;
			setAhead(5000 * moveDirection);
		} 
}
else{
		//square off
		setTurnRight(containBearing(enemy.getBearing() + 90));
	//change dir every .25 sec or on stop
		if (getTime() % 20 == 0||getVelocity() == 0) {
			moveDirection *= -1;
			setAhead(150 * moveDirection);
		}	

}
  }
  


  public void onBulletMissed(BulletMissedEvent e) {
   //reset enemy if keep missing
       count++;
    if (count == 3) {
      enemy.reset();
      count = 0;
    }
  }
  public void onBulletHit(BulletHitEvent e) {
    count = 0;
  }

  public void onHitWall(HitWallEvent e) {
   //find direction and dip
      setTurnRight(containBearing(e.getBearing() + 90));
	  setAhead(500 );
  }

  public void onScannedRobot(ScannedRobotEvent e) {
//if a sentry
    if (e.isSentryRobot() == true) {
      turnRight(e.getBearing());
      while ( e.getDistance() <= 50) {
        back(20);
      }
    } 
	//update enemy if none or closer than other enemy or is same enemy as tracked
	if ( enemy.none() || e.getDistance() < enemy.getDistance() - 20 ||enemy.getName().equals(e.getName())) {
			enemy.update(e);
		}

  }

//dip and update enemy
  public void onHitByBullet(HitByBulletEvent e) { 
     moveDirection*=-1;
	 moveMode*=1;
	 enemy.update(e);
  }

//reset enemy if its the one we are tracking
  public void onRobotDeath(RobotDeathEvent e) {
    	if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
  }

  public void initBearing() {
    // Calculate exact location of the robot
	//diffrence between  where the body is pointing and distance from the enemys tank
	//amount body needs to turn
    absoluteBearing = getHeading() + enemy.getBearing();
	//diffrence between th absoulte bearing - where the gun is pointing
	//amount gun needs to turn
    gunBearing = containBearing(absoluteBearing - getGunHeading());
  	//diffrence between th absoulte bearing - where the gun is pointing
	//amount radar needs to turn
    radarBearing = containBearing(absoluteBearing - getRadarHeading());
	
  }


  //contains in baring within +- 180 degrees
  double containBearing(double inputAngle) {
	  //positive side
    while (inputAngle > 180) inputAngle -= 360;
	//negtive side
    while (inputAngle < -180) inputAngle += 360;
    return inputAngle;
  }

}

class Opponent {
     private double bearing,distance,heading;
     private String name = "";
 

    public double getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getHeading() {
        return heading;
    }

  

   

    public void reset() {
        bearing = 0.0;
        distance = 0.0;
        heading = 0.0;
        name = "";
 
    }


    public boolean none() {
    
       if( name==""){
         return true;
       }
	   return false;
    }

    public void update(ScannedRobotEvent e) {
        bearing = e.getBearing();
        distance = e.getDistance();
        heading = e.getHeading();
        name = e.getName();
     
    }
	public void update(HitByBulletEvent e) {
        bearing = e.getBearing();
        heading = e.getHeading();
        name = e.getName();
		distance=0;
		
    }
	
	public void update(HitRobotEvent e){
	    heading=0;
        bearing = e.getBearing();
        distance=0;
        name = e.getName();
   }

   
}
