package ioio.examples.simple;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSimpleApp extends IOIOActivity {
	private TextView textView_;
	private TextView textView2_;
	private SeekBar seekBar_;
	private ToggleButton toggleButton_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textView_ = (TextView) findViewById(R.id.TextView);
		textView2_ = (TextView) findViewById(R.id.TextView1);
		seekBar_ = (SeekBar) findViewById(R.id.SeekBar);
		toggleButton_ = (ToggleButton) findViewById(R.id.ToggleButton);

		enableUi(false);
	}

	class Looper extends BaseIOIOLooper {
		private AnalogInput input_;
		private AnalogInput input_2;
		private PwmOutput pwmOutput_;
		private DigitalOutput led_;
		int new_value = 1000;

		@Override
		public void setup() throws ConnectionLostException {
			led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			input_ = ioio_.openAnalogInput(40); // Sensor left
			input_2 = ioio_.openAnalogInput(39); // Sensor right
			pwmOutput_ = ioio_.openPwmOutput(12, 100);
			enableUi(true);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			setNumber(input_.read());
			setNumber2(input_2.read());
			float value_left = input_.read();
			float value_right = input_2.read();

			//textView_.setText(String.format("%.2f", value_left));


			if (value_left > value_right+.10) {
				if (new_value < 2000){
					new_value += 20;}
				else {
					new_value = 2000;
				}
			}

			else if (.10+ value_left < value_right) {
				if (new_value > 1000) {
					new_value -= 20;
				}
				else{
					new_value = 1000;
				}
			}

			seekBar_.setProgress(new_value-1000);

			//int new_value = (int) (value * 1000) + 1000;
			//pwmOutput_.setPulseWidth(500 + seekBar_.getProgress() * 2);

			pwmOutput_.setPulseWidth(new_value);
			led_.write(!toggleButton_.isChecked());
			Thread.sleep(10);
		}

		@Override
		public void disconnected() {
			enableUi(false);
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seekBar_.setEnabled(enable);
				toggleButton_.setEnabled(enable);
			}
		});
	}

	private void setNumber(float f) {
		final String str = String.format("%.2f", f);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_.setText(str);
			}
		});
	}
	private void setNumber2(float f) {
		final String str = String.format("%.2f", f);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView2_.setText(str);
			}
		});
	}
}