/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react';
import Config from "../config.js";
import { ColorWheel } from 'react-native-color-wheel';
import ColorBar from "./color_bar.js";
import { hsvToRgb, rgbToHex } from 'colorsys';

import {
  StatusBar,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Input,
  TextInput,
} from 'react-native';




class SelectionScreen extends Component {

  state = {
    primaryRGB: {
      red: 255,
      green: 0,
      blue: 0,
    },
    secondaryRGB: {
      red: 0,
      green: 255,
      blue: 0,
    },
    tertiaryRGB: {
      red: 0,
      green: 0,
      blue: 255,
    },
    active: 0,
  }

  sendSignal = async (code) => {
    console.log(this.state.primaryRGB);
    console.log(code);
    let found = 0;
    try {
      // Handle timeout with a 5 second timer
      let abort = new AbortController();
      setTimeout(() => {
        if (found) return;
        new Error("Timeout: Time Exceeded");
      }, 5000)

      let res = await fetch(
        "http://" + Config.server_ip + ":" + Config.server_port + "/animate",
        {
          method: 'POST',
          signal: abort.signal,
          headers: {
            'Content-Type': "text/plain",
          },
          body: JSON.stringify({
            mode: code,
            primary: this.state.primaryRGB,
            secondary: this.state.secondaryRGB,
            tertiary: this.state.tertiaryRGB
          })
      });
      if (res) found = 1;       // Triggers the timeout to return without error

      let currAnim = await res.text() + "";       // Payload is returned as plain/text


    // Used to catch the timeout and other errors
    } catch (err) {
      console.log(err);
    }
  }

  setColors = (colors) => {
    let rgb = hsvToRgb(colors);
    //console.log(rgbToHex(rgb));
    //console.log(this.props.navigation.getParam('mode'));


    switch (this.state.active) {
      case 0:
        this.setState({
          primaryRGB: {
            red: rgb.r,
            green: rgb.g,
            blue: rgb.b
          },
        })
        break;
      case 1:
        this.setState({
          secondaryRGB: {
            red: rgb.r,
            green: rgb.g,
            blue: rgb.b
          },
        })
        break;
      case 2:
        this.setState({
          tertiaryRGB: {
            red: rgb.r,
            green: rgb.g,
            blue: rgb.b
          },
        })
        break;
    }
  }

  setRed = (color) => {
    let val;
    (color == "") ? val = 0 : val = parseInt(color);
    switch(this.state.active) {
      case 0:
        if (0 <= val && val <= 255) {
          this.setState({ primaryRGB: {...this.state.primaryRGB, red: val} });
        }
        break;
      case 1:
        if (0 <= val && val <= 255) {
          this.setState({ secondaryRGB: {...this.state.secondaryRGB, red: val} });
        }
        break;
      case 2:
        if (0 <= val && val <= 255) {
          this.setState({ tertiaryRGB: {...this.state.tertiaryRGB, red: val} });
        }
        break;
    }
  }

  setGreen = (color) => {
    let val;
    (color == "") ? val = 0 : val = parseInt(color);
    switch(this.state.active) {
      case 0:
        if (0 <= val && val <= 255) {
          this.setState({ primaryRGB: {...this.state.primaryRGB, green: val} });
        }
        break;
      case 1:
        if (0 <= val && val <= 255) {
          this.setState({ secondaryRGB: {...this.state.secondaryRGB, green: val} });
        }
        break;

      case 2:
        if (0 <= val && val <= 255) {
          this.setState({ tertiaryRGB: {...this.state.tertiaryRGB, green: val} });
        }
        break;
    }
  }

  setBlue = (color) => {
    let val;
    (color == "") ? val = 0 : val = parseInt(color);
    switch(this.state.active) {
      case 0:
        if (0 <= val && val <= 255) {
          this.setState({ primaryRGB: {...this.state.primaryRGB, blue: val} });
        }
        break;
      case 1:
        if (0 <= val && val <= 255) {
          this.setState({ secondaryRGB: {...this.state.secondaryRGB, blue: val} });
        }
        break;

      case 2:
        if (0 <= val && val <= 255) {
          this.setState({ tertiaryRGB: {...this.state.tertiaryRGB, blue: val} });
        }
        break;
    }
  }

  getRed = () => {
    switch (this.state.active) {
      case 0:
        return this.state.primaryRGB.red;
      case 1:
        return this.state.secondaryRGB.red;
      case 2:
        return this.state.tertiaryRGB.red;
    }
  }
  getGreen = () => {
    switch (this.state.active) {
      case 0:
        return this.state.primaryRGB.green;
      case 1:
        return this.state.secondaryRGB.green;
      case 2:
        return this.state.tertiaryRGB.green;
    }
  }
  getBlue = () => {
    switch (this.state.active) {
      case 0:
        return this.state.primaryRGB.blue;
      case 1:
        return this.state.secondaryRGB.blue;
      case 2:
        return this.state.tertiaryRGB.blue;
    }
  }

  getColorCode = () => {
    let intToHex = (val) => {
      let hexVal = val.toString(16);
      hexVal = (hexVal.length < 2) ? "0" + hexVal : hexVal;
      return hexVal;
    }
    let primaryHex = ("#" + intToHex(this.state.primaryRGB.red)
                          + intToHex(this.state.primaryRGB.green)
                          + intToHex(this.state.primaryRGB.blue)
                      );
    let secondaryHex = ("#" + intToHex(this.state.secondaryRGB.red)
                          + intToHex(this.state.secondaryRGB.green)
                          + intToHex(this.state.secondaryRGB.blue)
                      );
    let tertiaryHex = ("#" + intToHex(this.state.tertiaryRGB.red)
                          + intToHex(this.state.tertiaryRGB.green)
                          + intToHex(this.state.tertiaryRGB.blue)
                      );

    return { primaryHex, secondaryHex, tertiaryHex };

  }

  setColorState = (val) => {

    this.setState({
      ...this.state.active, active: val
    })
  }

  render() {
    const currColorCode = this.getColorCode();

    return (
      <View style={styles.container}>

        <View style={styles.inlineRow}>
          <TouchableOpacity
            name="Primary"
            style={this.state.active === 0 ? styles.buttonActive : styles.button}
            onPress={() => this.setColorState(0)}
          >
            <Text style={{color: "#FFFFFF"}}>Primary</Text>
          </TouchableOpacity>
          <TouchableOpacity
            name="Secondary"
            style={this.state.active === 1 ? styles.buttonActive : styles.button}
            onPress={() => this.setColorState(1)}
          >
            <Text style={{color: "#FFFFFF"}}>Secondary</Text>
          </TouchableOpacity>
          <TouchableOpacity
            name="Tertiary"
            style={this.state.active === 2 ? styles.buttonActive : styles.button}
            onPress={() => this.setColorState(2)}
          >
            <Text style={{color: "#FFFFFF"}}>Tertiary</Text>
          </TouchableOpacity>
        </View>

        <ColorWheel
          initialColor="#FF0000"
          onColorChange={color => this.setColors(color)}
          style={{width: 350}}
          thumbStyle={styles.colorThumbStyle}
          thumbSize={20}
        />


        <View style={styles.inlineRow}>
          <Text style={styles.inputHeader}>Red</Text>
          <Text style={styles.inputHeader}>Green</Text>
          <Text style={styles.inputHeader}>Blue</Text>
        </View>


        <View style={styles.inlineRow}>
          <View style={styles.inputContainer}>
            <TextInput
              style={styles.input}
              label="Red"
              onChangeText={(value) => this.setRed(value)}
              value={"" + this.getRed()}
              keyboardStyle='number-pad'
            />
          </View>
          <View style={styles.inputContainer}>
            <TextInput
              style={styles.input}
              onChangeText={(value) => this.setGreen(value)}
              value={"" + this.getGreen()}
              keyboardStyle='number-pad'
            />
            </View>
          <View style={styles.inputContainer}>
            <TextInput
              style={styles.input}
              onChangeText={(value) => this.setBlue(value)}
              value={"" + this.getBlue()}
              keyboardStyle='number-pad'
            />
          </View>
        </View>


        <ColorBar value="Primary Color" color={ currColorCode.primaryHex } />
        <ColorBar value="Secondary Color" color={ currColorCode.secondaryHex } />
        <ColorBar value="Tertiary Color" color={ currColorCode.tertiaryHex } />


        <TouchableOpacity
          name="Submit"
          onPress={() => this.sendSignal(this.props.navigation.getParam('mode'))}
          style={styles.submitButton}
        >
          <Text>Submit</Text>
        </TouchableOpacity>

      </View>
    )
  };
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFF0",
    //justifyContent: 'center',
    alignItems: 'center',
  },
  submitButton: {
    width: 350,
    height: 50,
    backgroundColor: '#88EEFF',
    justifyContent:'center',
    alignItems: 'center',
    borderRadius: 100,
    marginTop: 20,
    marginBottom: 20
  },
  button: {
    width: 100,
    height: 50,
    backgroundColor: '#0099ff',
    justifyContent:'center',
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 20,
    marginLeft: 10,
    marginRight: 10,
    borderRadius: 75
  },
  buttonActive: {
    width: 100,
    height: 50,
    backgroundColor: '#0000cc',
    justifyContent:'center',
    alignItems: 'center',
    marginTop: 20,
    marginLeft: 10,
    marginRight: 10,
    borderRadius: 75
  },
  colorThumbStyle: {
    width: 10,
    borderRadius: 10,
  },
  inlineRow: {
    flex: 0,
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
  input: {
    width: 100,
    height: 50,
    backgroundColor: "#EEEEEE",
    color: 'black',
    borderRadius: 20,
    textAlign: 'center',
    fontSize: 20
  },
  inputContainer: {
    flex: 1,
    width: 100,
    height: 50,
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    marginBottom: 20
  },
  inputHeader: {
    flex: 1,
    marginLeft: 50,
    flexDirection: 'row',
    justifyContent: 'space-evenly'
  }

});

export default SelectionScreen;
