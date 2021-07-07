/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react';
import Config from "../config.js";
import NavigationButton from "./navigation_button.js";

import {
  StatusBar,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
} from 'react-native';



class HomeScreen extends Component {
  state = {
    connectingText: "Connected",
    currAnimation: "",
  };

  animationLibrary = {
    0: "Off",
    1: "Chase",
    2: "Alternating RGB",
    3: "Red Fade",
    4: "Rainbow",
    5: "Rainbow With Glitter",
    6: "BPM"
  }

  // Send new animation data
  // Sets state to reflect changes in connection/animation
  sendSignal = async (code) => {
    let found = 0;
    try {

      // Handle timeout with a 5 second timer
      let abort = new AbortController();
      setTimeout(() => {
        if (found) return;
        new Error("Timeout: Time Exceeded");
      }, 5000)

      this.setState({
        currAnimation: this.currAnimation,
        connectingText: "Connecting...",
      })
      let res = await fetch(
        "http://" + Config.server_ip + ":" + Config.server_port + "/animate",
        {
          method: 'POST',
          signal: abort.signal,
          headers: {
            'Content-Type': "text/plain",
          },
          body: code
      });
      if (res) found = 1;       // Triggers the timeout to return without error

      let currAnim = await res.text() + "";       // Payload is returned as plain/text
      this.setState({
          connectingText: "Connected",
          currAnimation: this.animationLibrary[currAnim],
      });                            // data holds the current animation from the ESP


    // Used to catch the timeout and other errors
    } catch (err) {

      this.setState({
        connectingText: "Connection Failed",
        currAnimation: this.state.currAnimation
      });
    }
  };

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity
          style={styles.button}
          onPress={() => this.sendSignal("1")}
        >
          <Text>Off</Text>
        </TouchableOpacity>
        <NavigationButton mode={1} value={"Chase"} navigation={this.props.navigation}/>
        <NavigationButton mode={2} value={"Alternating"} navigation={this.props.navigation}/>
        <NavigationButton mode={3} value={"Random Fade"} navigation={this.props.navigation}/>
        <NavigationButton mode={4} value={"Rainbow"} navigation={this.props.navigation}/>
        <NavigationButton mode={5} value={"Rainbow With Glitter"} navigation={this.props.navigation}/>
        <NavigationButton mode={6} value={"BPM"} navigation={this.props.navigation}/>
        <View>
          <Text>Current Animation: { this.state.currAnimation }</Text>
        </View>
        <View>
          <Text>Connection Status: { this.state.connectingText }</Text>
        </View>
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
  button: {
    width: 250,
    height: 50,
    backgroundColor: '#8BF9EF',
    justifyContent:'center',
    alignItems: 'center',
    marginTop: 20,
  },
})

export default HomeScreen;
