# Affichage des résultats de XP0_TestConsigne
# ou Génération des fichiers et création de PNG
#
# ************** EXEMPLE AFFICHAGE **************************
# Rscript plotTestConsigne.R visu test_cons-a0_10.0-a1_25.0-im0-cv000.10000-ct000.30000_*.data
#
# ************** EXEMPLE GENERATION *************************
# Rscript plotTestConsigne.R generate
#
# ***********************************************************

plotTestConsigne <- function( file, muscle )
{
  mat <- read.table( file, header=TRUE)
  
  def.par <- par(no.readonly = TRUE) # save default, for resetting...
  
  # paste '.png' après avoir enlevé l'extension
  pngfile <- paste(sub("\\.[^.]*$", "", file),".png",sep="") 
  print(paste("png =>", pngfile))
  
  png(pngfile)
  # Multiplot : size of matrix determines Grid size
  layout( matrix(c(1,2)), # pos of fig (1col,2rows)
          #height=c(0.8,0.2) # (relative) heights
  )
  par(mar=c(2,4,2,1)) # bot,left,top,right
  
  # ArmEnd avec petits ronds noirs, départ=croix rouge
  plot( mat$ArmEndX, mat$ArmEndY, pch=20, col=1)
  points( mat$ArmEndX[1],mat$ArmEndY[1],pch=4,col=2,cex=2)
  title(main='TestConsigne')
  
  # temps, consigne, activité, torque0,1
  matplot( mat$time, cbind( mat[4+muscle], mat[10+muscle],
                            mat$torq0, mat$torq1), type="l", lty=1)

  legend(x='topright',legend=c('cons','act','tor0','tor2'),
         lty=c(1,1,1,1),col=1:4)
  
  par(def.par)  #- reset to default
  #dev.copy(png,'myplot.png')
  dev.off()
}

plotTestConsigneAll <- function( listFile, muscle )
{
  listMat <- list()
  for( i  in 1:length(listFile) ) {
    listMat[[i]] <- read.table( listFile[[i]], header=TRUE)
  }
  # paste '.png' après avoir enlevé l'extension
  pngfile <- paste(sub("_[[:digit:]]*\\.[^.]*$", "", listFile[[1]]),"_tot.png",sep="") 
  print(paste("pngALL =>",pngfile))
  
  png(pngfile)
  
  def.par <- par(no.readonly = TRUE) # save default, for resetting...
  # Multiplot : size of matrix determines Grid size
  layout( matrix(c(1,2)), # pos of fig (1col,2rows)
          #height=c(0.8,0.2) # (relative) heights
  )
  par(mar=c(2,4,2,1)) # bot,left,top,right
  
  # Prépare les axes et le reste
  mat <- listMat[[1]]
  plot( mat$ArmEndX, mat$ArmEndY, pch=20, col=1, type="n")
  
  for( mat in listMat ) {
    points( mat$ArmEndX, mat$ArmEndY, pch=20, col=1 )
  }
  points( mat$ArmEndX[1],mat$ArmEndY[1],pch=4,col=2,cex=2)
  
  # Prépare les axes et le reste
  # temps, consigne, activité
  mat <- listMat[[1]]
  matplot( mat$time, cbind( mat[4+muscle], mat[10+muscle]), type="l", lty=1)
  for( mat in listMat ) {
    matlines( mat$time, cbind( mat[4+muscle], mat[10+muscle]), type="l", lty=1)
  }
  legend(x='topright',legend=c('cons','act'),
         lty=c(1,1,1,1),col=1:4)
  
  par(def.par)  #- reset to default
  dev.off()
  
}
generateCommand <- function() {
  angList <- list( c(10,25), c(15,30), c(35,120))
  muscleList <- 0:5
  consList <- c(0.001,0.02,0.05,0.1,0.5)
  nbXP <- 5
  for (ang in angList) {
    for (muscle in muscleList) {
      for (cons in consList) {
        # Run simulations in Java
        runComm <- paste("./run.sh simulator.Main simulator.XP0_TestConsigne",
                         "-a0",ang[1], "-a1",ang[2],
                         "-im",muscle, "-cv",cons, "-ct",0.3,
                         "-lf test_cons", "-n", nbXP, "-mt", 1,
                         sep=" ")
        print(runComm)
        system( runComm )
        
        # Generate the .png file for visualisation
        logFileBase <- paste("test_cons",
                             "-a0_",format(ang[1],nsmall=1), "-a1_",format(ang[2],nsmall=1),
                             "-im",muscle,
                             "-cv",format(cons, nsmall=3), "-ct",format(0.3, nsmall=3),
                             "_\\d*.data", sep="")
        fileList <- dir( pattern=logFileBase )
#         print( paste("PATTERN=",logFileBase) )
        print( paste("TO_PNG=",fileList ) )
#         for( filename in fileList ) {
#           plotTestConsigne( filename, muscle )
#         }
        plotTestConsigneAll( fileList, muscle )
      }
    }
  }
}

## Lecture des arguments
arg <- commandArgs()
print(arg)

if (length(arg) < 6) {
  print("usage: Rscript plotTestConsigne.R visu muscle files")
  print("usage: Rscript plotTestConsigne.R generate")
} else {
  if (arg[6] == "visu") {
    for( name in arg[8:length(arg)]) {
      plotTestConsigne( name, muscle=as.numeric(arg[7]) )
    }
    plotTestConsigneAll(arg[8:length(arg)], muscle=as.numeric(arg[7]))
  } else {
    if (arg[6] == "generate") {
      generateCommand()
    } else {
      print("usage : command must be visu OU generate")
    }
  }
}




