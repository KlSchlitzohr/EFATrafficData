package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.Ref;
import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Point {

   private String name;
   private String nameWO;
   private String platformName;
   private String plannedPlatformName;
   private String place;
   private String usage;
   private String omc;
   private String placeID;
   private String desc;
   private Stamp stamp;
   private DateTime dateTime;
   private Ref ref;

}
