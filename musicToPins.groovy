//Your code here
import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;

double pinSize = 10

int NOTE_ON = 0x90;
int NOTE_OFF = 0x80;
def NOTE_NAMES = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"] as String [];
//create a sphere
CSG sphere = new Sphere(pinSize/2)// Spheres radius
				.toCSG()// convert to CSG to display
// A list of nubs to return 
ArrayList<CSG> nubs = new ArrayList<>()

Sequence sequence = MidiSystem.getSequence(new File("/usr/lib/pd/extra/cyclone/test.mid"));

int trackNumber = 0;
for (Track track :  sequence.getTracks()) {
  trackNumber++;
 
  double permiter = pinSize*2*track.size()
  double radius = (permiter/Math.PI)/2
  int lastTick = track.get(track.size()-1).getTick()
  System.out.println("Track " + trackNumber + ": size = " + track.size()+" song radius "+radius+"  last event tick: " +lastTick);
  System.out.println();
  nubs.add(new Cylinder(radius-pinSize/2,radius-pinSize/2,pinSize,(int)30).toCSG());
  for (int i=0; i < track.size(); i++) { 
      MidiEvent event = track.get(i);
      System.out.print("@" + event.getTick() + " ");
      MidiMessage message = event.getMessage();
      if (message instanceof ShortMessage) {
          ShortMessage sm = (ShortMessage) message;
          System.out.print("Channel: " + sm.getChannel() + " ");
          if (sm.getCommand() == NOTE_ON) {
              int key = sm.getData1();
              int octave = (key / 12)-1;
              int note = key % 12;
              String noteName = NOTE_NAMES[note];
              int velocity = sm.getData2();
              System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
              nubs.add(sphere
					.movey((note*pinSize)+radius)
					.rotz((360/lastTick)*event.getTick())
					)
          } else if (sm.getCommand() == NOTE_OFF) {
              int key = sm.getData1();
              int octave = (key / 12)-1;
              int note = key % 12;
              String noteName = NOTE_NAMES[note];
              int velocity = sm.getData2();
              System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
          } else {
              System.out.println("Command:" + sm.getCommand());
          }
      } else {
          System.out.println("Other message: " + message.getClass());
      }
  }

  System.out.println();
}

Sequencer sequencer = MidiSystem.getSequencer();
sequencer.open();
sequencer.setSequence(sequence);

// Start playing
sequencer.start();


return nubs
