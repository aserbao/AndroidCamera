/* ========================================================================================== */
/* FMOD Studio - Built-in effects header file.                                                */
/* Copyright (c), Firelight Technologies Pty, Ltd. 2004-2017.                                 */
/*                                                                                            */
/* In this header you can find parameter structures for FMOD system registered DSP effects    */
/* and generators.                                                                            */
/*                                                                                            */
/* ========================================================================================== */

#ifndef _FMOD_DSP_EFFECTS_H
#define _FMOD_DSP_EFFECTS_H

/*
[ENUM]
[
    [DESCRIPTION]
    These definitions can be used for creating FMOD defined special effects or DSP units.

    [REMARKS]
    To get them to be active, first create the unit, then add it somewhere into the DSP network, 
    either at the front of the network near the soundcard unit to affect the global output
    (by using System::getDSPHead), or on a single channel (using Channel::getDSPHead).

    [SEE_ALSO]
    System::createDSPByType
]
*/
typedef enum
{
    FMOD_DSP_TYPE_UNKNOWN,            /* This unit was created via a non FMOD plugin so has an unknown purpose. */
    FMOD_DSP_TYPE_MIXER,              /* This unit does nothing but take inputs and mix them together then feed the result to the soundcard unit. */
    FMOD_DSP_TYPE_OSCILLATOR,         /* This unit generates sine/square/saw/triangle or noise tones. */
    FMOD_DSP_TYPE_LOWPASS,            /* This unit filters sound using a high quality, resonant lowpass filter algorithm but consumes more CPU time. Deprecated and will be removed in a future release (see FMOD_DSP_LOWPASS remarks for alternatives). */
    FMOD_DSP_TYPE_ITLOWPASS,          /* This unit filters sound using a resonant lowpass filter algorithm that is used in Impulse Tracker, but with limited cutoff range (0 to 8060hz). */
    FMOD_DSP_TYPE_HIGHPASS,           /* This unit filters sound using a resonant highpass filter algorithm. Deprecated and will be removed in a future release (see FMOD_DSP_HIGHPASS remarks for alternatives). */
    FMOD_DSP_TYPE_ECHO,               /* This unit produces an echo on the sound and fades out at the desired rate. */
    FMOD_DSP_TYPE_FADER,              /* This unit pans and scales the volume of a unit. */
    FMOD_DSP_TYPE_FLANGE,             /* This unit produces a flange effect on the sound. */
    FMOD_DSP_TYPE_DISTORTION,         /* This unit distorts the sound. */
    FMOD_DSP_TYPE_NORMALIZE,          /* This unit normalizes or amplifies the sound to a certain level. */
    FMOD_DSP_TYPE_LIMITER,            /* This unit limits the sound to a certain level. */
    FMOD_DSP_TYPE_PARAMEQ,            /* This unit attenuates or amplifies a selected frequency range. Deprecated and will be removed in a future release (see FMOD_DSP_PARAMEQ remarks for alternatives). */
    FMOD_DSP_TYPE_PITCHSHIFT,         /* This unit bends the pitch of a sound without changing the speed of playback. */
    FMOD_DSP_TYPE_CHORUS,             /* This unit produces a chorus effect on the sound. */
    FMOD_DSP_TYPE_VSTPLUGIN,          /* This unit allows the use of Steinberg VST plugins */
    FMOD_DSP_TYPE_WINAMPPLUGIN,       /* This unit allows the use of Nullsoft Winamp plugins */
    FMOD_DSP_TYPE_ITECHO,             /* This unit produces an echo on the sound and fades out at the desired rate as is used in Impulse Tracker. */
    FMOD_DSP_TYPE_COMPRESSOR,         /* This unit implements dynamic compression (linked/unlinked multichannel, wideband) */
    FMOD_DSP_TYPE_SFXREVERB,          /* This unit implements SFX reverb */
    FMOD_DSP_TYPE_LOWPASS_SIMPLE,     /* This unit filters sound using a simple lowpass with no resonance, but has flexible cutoff and is fast. Deprecated and will be removed in a future release (see FMOD_DSP_LOWPASS_SIMPLE remarks for alternatives). */
    FMOD_DSP_TYPE_DELAY,              /* This unit produces different delays on individual channels of the sound. */
    FMOD_DSP_TYPE_TREMOLO,            /* This unit produces a tremolo / chopper effect on the sound. */
    FMOD_DSP_TYPE_LADSPAPLUGIN,       /* Unsupported / Deprecated. */
    FMOD_DSP_TYPE_SEND,               /* This unit sends a copy of the signal to a return DSP anywhere in the DSP tree. */
    FMOD_DSP_TYPE_RETURN,             /* This unit receives signals from a number of send DSPs. */
    FMOD_DSP_TYPE_HIGHPASS_SIMPLE,    /* This unit filters sound using a simple highpass with no resonance, but has flexible cutoff and is fast. Deprecated and will be removed in a future release (see FMOD_DSP_HIGHPASS_SIMPLE remarks for alternatives). */
    FMOD_DSP_TYPE_PAN,                /* This unit pans the signal, possibly upmixing or downmixing as well. */
    FMOD_DSP_TYPE_THREE_EQ,           /* This unit is a three-band equalizer. */
    FMOD_DSP_TYPE_FFT,                /* This unit simply analyzes the signal and provides spectrum information back through getParameter. */
    FMOD_DSP_TYPE_LOUDNESS_METER,     /* This unit analyzes the loudness and true peak of the signal. */
    FMOD_DSP_TYPE_ENVELOPEFOLLOWER,   /* This unit tracks the envelope of the input/sidechain signal. Format to be publicly disclosed soon. */
    FMOD_DSP_TYPE_CONVOLUTIONREVERB,  /* This unit implements convolution reverb. */
    FMOD_DSP_TYPE_CHANNELMIX,         /* This unit provides per signal channel gain, and output channel mapping to allow 1 multichannel signal made up of many groups of signals to map to a single output signal. */
    FMOD_DSP_TYPE_TRANSCEIVER,        /* This unit 'sends' and 'receives' from a selection of up to 32 different slots.  It is like a send/return but it uses global slots rather than returns as the destination.  It also has other features.  Multiple transceivers can receive from a single channel, or multiple transceivers can send to a single channel, or a combination of both. */
    FMOD_DSP_TYPE_OBJECTPAN,          /* This unit sends the signal to a 3d object encoder like Dolby Atmos.   Supports a subset of the FMOD_DSP_TYPE_PAN parameters. */
    FMOD_DSP_TYPE_MULTIBAND_EQ,       /* This unit is a flexible five band parametric equalizer. */

    FMOD_DSP_TYPE_MAX,                /* Maximum number of pre-defined DSP types. */
    FMOD_DSP_TYPE_FORCEINT = 65536    /* Makes sure this enum is signed 32bit. */
} FMOD_DSP_TYPE;

/*
    ===================================================================================================

    FMOD built in effect parameters.  
    Use DSP::setParameter with these enums for the 'index' parameter.

    ===================================================================================================
*/

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_OSCILLATOR filter.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::setParameterInt
    DSP::getParameterFloat
    DSP::getParameterInt
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_OSCILLATOR_TYPE,   /* (Type:int) - Waveform type.  0 = sine.  1 = square. 2 = sawup. 3 = sawdown. 4 = triangle. 5 = noise.  */
    FMOD_DSP_OSCILLATOR_RATE    /* (Type:float) - Frequency of the sinewave in hz.  1.0 to 22000.0.  Default = 220.0. */
} FMOD_DSP_OSCILLATOR;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_LOWPASS filter.

    [REMARKS]
    Deprecated and will be removed in a future release, to emulate with FMOD_DSP_TYPE_MULTIBAND_EQ:

        // Configure a single band (band A) as a lowpass (all other bands default to off).
        // 24dB rolloff to approximate the old effect curve.
        // Cutoff frequency can be used the same as with the old effect.
        // Resonance can be applied by setting the 'Q' value of the new effect.
        FMOD_DSP_SetParameterInt(multiband, FMOD_DSP_MULTIBAND_EQ_A_FILTER, FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_24DB);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, frequency);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_Q, resonance);

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_LOWPASS_CUTOFF,    /* (Type:float) - Lowpass cutoff frequency in hz.   10.0 to 22000.0.  Default = 5000.0. */
    FMOD_DSP_LOWPASS_RESONANCE  /* (Type:float) - Lowpass resonance Q value. 1.0 to 10.0.  Default = 1.0. */
} FMOD_DSP_LOWPASS;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_ITLOWPASS filter.<br>
    This is different to the default FMOD_DSP_TYPE_ITLOWPASS filter in that it uses a different quality algorithm and is 
    the filter used to produce the correct sounding playback in .IT files.<br> 
    FMOD Studio's .IT playback uses this filter.<br>

    [REMARKS]
    Note! This filter actually has a limited cutoff frequency below the specified maximum, due to its limited design, 
    so for a more  open range filter use FMOD_DSP_LOWPASS or if you don't mind not having resonance, 
    FMOD_DSP_LOWPASS_SIMPLE.<br>
    The effective maximum cutoff is about 8060hz.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_ITLOWPASS_CUTOFF,    /* (Type:float) - Lowpass cutoff frequency in hz.  1.0 to 22000.0.  Default = 5000.0/ */
    FMOD_DSP_ITLOWPASS_RESONANCE  /* (Type:float) - Lowpass resonance Q value.  0.0 to 127.0.  Default = 1.0. */
} FMOD_DSP_ITLOWPASS;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_HIGHPASS filter.

    [REMARKS]
    Deprecated and will be removed in a future release, to emulate with FMOD_DSP_TYPE_MULTIBAND_EQ:

        // Configure a single band (band A) as a highpass (all other bands default to off).
        // 12dB rolloff to approximate the old effect curve.
        // Cutoff frequency can be used the same as with the old effect.
        // Resonance can be applied by setting the 'Q' value of the new effect.
        FMOD_DSP_SetParameterInt(multiband, FMOD_DSP_MULTIBAND_EQ_A_FILTER, FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHPASS_12DB);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, frequency);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_Q, resonance);

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_HIGHPASS_CUTOFF,    /* (Type:float) - Highpass cutoff frequency in hz.  1.0 to output 22000.0.  Default = 5000.0. */
    FMOD_DSP_HIGHPASS_RESONANCE  /* (Type:float) - Highpass resonance Q value.  1.0 to 10.0.  Default = 1.0. */
} FMOD_DSP_HIGHPASS;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_ECHO filter.

    [REMARKS]
    Note.  Every time the delay is changed, the plugin re-allocates the echo buffer.  This means the echo will dissapear at that time while it refills its new buffer.<br>
    Larger echo delays result in larger amounts of memory allocated.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_ECHO_DELAY,       /* (Type:float) - Echo delay in ms.  10  to 5000.  Default = 500. */
    FMOD_DSP_ECHO_FEEDBACK,    /* (Type:float) - Echo decay per delay.  0 to 100.  100.0 = No decay, 0.0 = total decay (ie simple 1 line delay).  Default = 50.0. */
    FMOD_DSP_ECHO_DRYLEVEL,    /* (Type:float) - Original sound volume in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_ECHO_WETLEVEL     /* (Type:float) - Volume of echo signal to pass to output in dB.  -80.0 to 10.0.  Default = 0. */
} FMOD_DSP_ECHO;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_FLANGE filter.

    [REMARKS]
    Flange is an effect where the signal is played twice at the same time, and one copy slides back and forth creating a whooshing or flanging effect.<br>
    As there are 2 copies of the same signal, by default each signal is given 50% mix, so that the total is not louder than the original unaffected signal.<br>
    <br>
    Flange depth is a percentage of a 10ms shift from the original signal.  Anything above 10ms is not considered flange because to the ear it begins to 'echo' so 10ms is the highest value possible.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_FLANGE_MIX,         /* (Type:float) - Percentage of wet signal in mix.  0 to 100. Default = 50. */
    FMOD_DSP_FLANGE_DEPTH,       /* (Type:float) - Flange depth (percentage of 40ms delay).  0.01 to 1.0.  Default = 1.0. */
    FMOD_DSP_FLANGE_RATE         /* (Type:float) - Flange speed in hz.  0.0 to 20.0.  Default = 0.1. */
} FMOD_DSP_FLANGE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_DISTORTION filter.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_DISTORTION_LEVEL    /* (Type:float) - Distortion value.  0.0 to 1.0.  Default = 0.5. */
} FMOD_DSP_DISTORTION;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_NORMALIZE filter.

    [REMARKS]
    Normalize amplifies the sound based on the maximum peaks within the signal.<br>
    For example if the maximum peaks in the signal were 50% of the bandwidth, it would scale the whole sound by 2.<br>
    The lower threshold value makes the normalizer ignores peaks below a certain point, to avoid over-amplification if a loud signal suddenly came in, and also to avoid amplifying to maximum things like background hiss.<br>
    <br>
    Because FMOD is a realtime audio processor, it doesn't have the luxury of knowing the peak for the whole sound (ie it can't see into the future), so it has to process data as it comes in.<br>
    To avoid very sudden changes in volume level based on small samples of new data, fmod fades towards the desired amplification which makes for smooth gain control.  The fadetime parameter can control this.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_NORMALIZE_FADETIME,    /* (Type:float) - Time to ramp the silence to full in ms.  0.0 to 20000.0. Default = 5000.0. */
    FMOD_DSP_NORMALIZE_THRESHHOLD,  /* (Type:float) - Lower volume range threshold to ignore.  0.0 to 1.0.  Default = 0.1.  Raise higher to stop amplification of very quiet signals. */
    FMOD_DSP_NORMALIZE_MAXAMP       /* (Type:float) - Maximum amplification allowed.  1.0 to 100000.0.  Default = 20.0.  1.0 = no amplifaction, higher values allow more boost. */
} FMOD_DSP_NORMALIZE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_LIMITER filter.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_LIMITER_RELEASETIME,   /* (Type:float) - Time to ramp the silence to full in ms.  1.0 to 1000.0. Default = 10.0. */
    FMOD_DSP_LIMITER_CEILING,       /* (Type:float) - Maximum level of the output signal in dB.  -12.0 to 0.0.  Default = 0.0. */
    FMOD_DSP_LIMITER_MAXIMIZERGAIN, /* (Type:float) - Maximum amplification allowed in dB.  0.0 to 12.0.  Default = 0.0. 0.0 = no amplifaction, higher values allow more boost. */
    FMOD_DSP_LIMITER_MODE,          /* (Type:float) - Channel processing mode. 0 or 1. Default = 0. 0 = Independent (limiter per channel), 1 = Linked. */
} FMOD_DSP_LIMITER;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_PARAMEQ filter.

    [REMARKS]
    Deprecated and will be removed in a future release, to emulate with FMOD_DSP_TYPE_MULTIBAND_EQ:

        // Configure a single band (band A) as a peaking EQ (all other bands default to off).
        // Center frequency can be used as with the old effect.
        // Bandwidth can be applied by setting the 'Q' value of the new effect.
        // Gain at the center frequency can be used the same as with the old effect.
        FMOD_DSP_SetParameterInt(multiband, FMOD_DSP_MULTIBAND_EQ_A_FILTER, FMOD_DSP_MULTIBAND_EQ_FILTER_PEAKING);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, center);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_Q, bandwidth);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_GAIN, gain);

    Parametric EQ is a single band peaking EQ filter that attenuates or amplifies a selected frequency and its neighbouring frequencies.

    When a frequency has its gain set to 1.0, the sound will be unaffected and represents the original signal exactly.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_PARAMEQ_CENTER,     /* (Type:float) - Frequency center.  20.0 to 22000.0.  Default = 8000.0. */
    FMOD_DSP_PARAMEQ_BANDWIDTH,  /* (Type:float) - Octave range around the center frequency to filter.  0.2 to 5.0.  Default = 1.0. */
    FMOD_DSP_PARAMEQ_GAIN        /* (Type:float) - Frequency Gain in dB.  -30 to 30.  Default = 0.  */
} FMOD_DSP_PARAMEQ;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_MULTIBAND_EQ filter.

    [REMARKS]
    Flexible five band parametric equalizer.

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum FMOD_DSP_MULTIBAND_EQ
{
    FMOD_DSP_MULTIBAND_EQ_A_FILTER,    /* (Type:int)   - Band A: FMOD_DSP_MULTIBAND_EQ_FILTER_TYPE used to interpret the behavior of the remaining parameters. Default = FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_12DB */
    FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, /* (Type:float) - Band A: Significant frequency in Hz, cutoff [low/high pass, low/high shelf], center [notch, peaking, band-pass], phase transition point [all-pass]. 20 to 22000. Default = 8000. */
    FMOD_DSP_MULTIBAND_EQ_A_Q,         /* (Type:float) - Band A: Quality factor, resonance [low/high pass], bandwidth [notch, peaking, band-pass], phase transition sharpness [all-pass], unused [low/high shelf]. 0.1 to 10.0. Default = 0.707. */
    FMOD_DSP_MULTIBAND_EQ_A_GAIN,      /* (Type:float) - Band A: Boost or attenuation in dB [peaking, high/low shelf only]. -30 to 30. Default = 0. */
    FMOD_DSP_MULTIBAND_EQ_B_FILTER,    /* (Type:int)   - Band B: See Band A. Default = FMOD_DSP_MULTIBAND_EQ_FILTER_DISABLED */
    FMOD_DSP_MULTIBAND_EQ_B_FREQUENCY, /* (Type:float) - Band B: See Band A */
    FMOD_DSP_MULTIBAND_EQ_B_Q,         /* (Type:float) - Band B: See Band A */
    FMOD_DSP_MULTIBAND_EQ_B_GAIN,      /* (Type:float) - Band B: See Band A */
    FMOD_DSP_MULTIBAND_EQ_C_FILTER,    /* (Type:int)   - Band C: See Band A. Default = FMOD_DSP_MULTIBAND_EQ_FILTER_DISABLED */
    FMOD_DSP_MULTIBAND_EQ_C_FREQUENCY, /* (Type:float) - Band C: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_C_Q,         /* (Type:float) - Band C: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_C_GAIN,      /* (Type:float) - Band C: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_D_FILTER,    /* (Type:int)   - Band D: See Band A. Default = FMOD_DSP_MULTIBAND_EQ_FILTER_DISABLED */
    FMOD_DSP_MULTIBAND_EQ_D_FREQUENCY, /* (Type:float) - Band D: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_D_Q,         /* (Type:float) - Band D: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_D_GAIN,      /* (Type:float) - Band D: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_E_FILTER,    /* (Type:int)   - Band E: See Band A. Default = FMOD_DSP_MULTIBAND_EQ_FILTER_DISABLED */
    FMOD_DSP_MULTIBAND_EQ_E_FREQUENCY, /* (Type:float) - Band E: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_E_Q,         /* (Type:float) - Band E: See Band A. */
    FMOD_DSP_MULTIBAND_EQ_E_GAIN,      /* (Type:float) - Band E: See Band A. */
} FMOD_DSP_MULTIBAND_EQ;


/*
[ENUM]
[
    [DESCRIPTION]
    Filter types for FMOD_DSP_MULTIBAND_EQ.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_MULTIBAND_EQ
]
*/
typedef enum FMOD_DSP_MULTIBAND_EQ_FILTER_TYPE
{
    FMOD_DSP_MULTIBAND_EQ_FILTER_DISABLED,       /* Disabled filter, no processing. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_12DB,   /* Resonant low-pass filter, attenuates frequencies (12dB per octave) above a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_24DB,   /* Resonant low-pass filter, attenuates frequencies (24dB per octave) above a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_48DB,   /* Resonant low-pass filter, attenuates frequencies (48dB per octave) above a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHPASS_12DB,  /* Resonant low-pass filter, attenuates frequencies (12dB per octave) below a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHPASS_24DB,  /* Resonant low-pass filter, attenuates frequencies (24dB per octave) below a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHPASS_48DB,  /* Resonant low-pass filter, attenuates frequencies (48dB per octave) below a given point (with specificed resonance) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_LOWSHELF,       /* Low-shelf filter, boosts or attenuates frequencies (with specified gain) below a given point while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHSHELF,      /* High-shelf filter, boosts or attenuates frequencies (with specified gain) above a given point while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_PEAKING,        /* Peaking filter, boosts or attenuates frequencies (with specified gain) at a given point (with specificed bandwidth) while allowing the rest to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_BANDPASS,       /* Band-pass filter, allows frequencies at a given point (with specificed bandwidth) to pass while attenuating frequencies outside this range. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_NOTCH,          /* Notch or band-reject filter, attenuates frequencies at a given point (with specificed bandwidth) while allowing frequencies outside this range to pass. */
    FMOD_DSP_MULTIBAND_EQ_FILTER_ALLPASS,        /* All-pass filter, allows all frequencies to pass, but changes the phase response at a given point (with specified sharpness). */
} FMOD_DSP_MULTIBAND_EQ_FILTER_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_PITCHSHIFT filter.

    [REMARKS]
    This pitch shifting unit can be used to change the pitch of a sound without speeding it up or slowing it down.<br>
    It can also be used for time stretching or scaling, for example if the pitch was doubled, and the frequency of the sound was halved, the pitch of the sound would sound correct but it would be twice as slow.<br>
    <br>
    <b>Warning!</b> This filter is very computationally expensive!  Similar to a vocoder, it requires several overlapping FFT and IFFT's to produce smooth output, and can require around 440mhz for 1 stereo 48khz signal using the default settings.<br>
    Reducing the signal to mono will half the cpu usage.<br>
    Reducing this will lower audio quality, but what settings to use are largely dependant on the sound being played.  A noisy polyphonic signal will need higher fft size compared to a speaking voice for example.<br>
    <br>
    This pitch shifter is based on the pitch shifter code at http://www.dspdimension.com, written by Stephan M. Bernsee.<br>
    The original code is COPYRIGHT 1999-2003 Stephan M. Bernsee <smb@dspdimension.com>.<br>
    <br>
    '<i>maxchannels</i>' dictates the amount of memory allocated.  By default, the maxchannels value is 0.  If FMOD is set to stereo, the pitch shift unit will allocate enough memory for 2 channels.  If it is 5.1, it will allocate enough memory for a 6 channel pitch shift, etc.<br>
    If the pitch shift effect is only ever applied to the global mix (ie it was added with ChannelGroup::addDSP), then 0 is the value to set as it will be enough to handle all speaker modes.<br>
    When the pitch shift is added to a channel (ie Channel::addDSP) then the channel count that comes in could be anything from 1 to 8 possibly.  It is only in this case where you might want to increase the channel count above the output's channel count.<br>
    If a channel pitch shift is set to a lower number than the sound's channel count that is coming in, it will not pitch shift the sound.<br>
    <br>
    <b>NOTE!</b> Not supported on PlayStation 3.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    ChannelGroup::addDSP
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_PITCHSHIFT_PITCH,       /* (Type:float) - Pitch value.  0.5 to 2.0.  Default = 1.0. 0.5 = one octave down, 2.0 = one octave up.  1.0 does not change the pitch. */
    FMOD_DSP_PITCHSHIFT_FFTSIZE,     /* (Type:float) - FFT window size.  256, 512, 1024, 2048, 4096.  Default = 1024.  Increase this to reduce 'smearing'.  This effect is a warbling sound similar to when an mp3 is encoded at very low bitrates. */
    FMOD_DSP_PITCHSHIFT_OVERLAP,     /* (Type:float) - Removed.  Do not use.  FMOD now uses 4 overlaps and cannot be changed. */
    FMOD_DSP_PITCHSHIFT_MAXCHANNELS  /* (Type:float) - Maximum channels supported.  0 to 16.  0 = same as fmod's default output polyphony, 1 = mono, 2 = stereo etc.  See remarks for more.  Default = 0.  It is suggested to leave at 0! */
} FMOD_DSP_PITCHSHIFT;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_CHORUS filter.

    [REMARKS]
    Chorous is an effect where the sound is more 'spacious' due to 1 to 3 versions of the sound being played along side the original signal but with the pitch of each copy modulating on a sine wave.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_CHORUS_MIX,      /* (Type:float) - Volume of original signal to pass to output.  0.0 to 100.0. Default = 50.0. */
    FMOD_DSP_CHORUS_RATE,     /* (Type:float) - Chorus modulation rate in Hz.  0.0 to 20.0.  Default = 0.8 Hz. */
    FMOD_DSP_CHORUS_DEPTH,    /* (Type:float) - Chorus modulation depth.  0.0 to 100.0.  Default = 3.0. */
} FMOD_DSP_CHORUS;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_ITECHO filter.<br>
    This is effectively a software based echo filter that emulates the DirectX DMO echo effect.  Impulse tracker files can support this, and FMOD will produce the effect on ANY platform, not just those that support DirectX effects!<br>

    [REMARKS]
    Note.  Every time the delay is changed, the plugin re-allocates the echo buffer.  This means the echo will dissapear at that time while it refills its new buffer.<br>
    Larger echo delays result in larger amounts of memory allocated.<br>
    <br>
    As this is a stereo filter made mainly for IT playback, it is targeted for stereo signals.<br>
    With mono signals only the FMOD_DSP_ITECHO_LEFTDELAY is used.<br>
    For multichannel signals (>2) there will be no echo on those channels.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_ITECHO_WETDRYMIX,      /* (Type:float) - Ratio of wet (processed) signal to dry (unprocessed) signal. Must be in the range from 0.0 through 100.0 (all wet).  Default = 50. */
    FMOD_DSP_ITECHO_FEEDBACK,       /* (Type:float) - Percentage of output fed back into input, in the range from 0.0 through 100.0.  Default = 50. */
    FMOD_DSP_ITECHO_LEFTDELAY,      /* (Type:float) - Delay for left channel, in milliseconds, in the range from 1.0 through 2000.0.  Default = 500 ms. */
    FMOD_DSP_ITECHO_RIGHTDELAY,     /* (Type:float) - Delay for right channel, in milliseconds, in the range from 1.0 through 2000.0.  Default = 500 ms. */
    FMOD_DSP_ITECHO_PANDELAY        /* (Type:float) - Value that specifies whether to swap left and right delays with each successive echo.  Ranges from 0.0 (equivalent to FALSE) to 1.0 (equivalent to TRUE), meaning no swap.  Default = 0.  CURRENTLY NOT SUPPORTED. */
} FMOD_DSP_ITECHO;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_COMPRESSOR unit.
    This is a multichannel software limiter that is uniform across the whole spectrum.

    [REMARKS]
    The limiter is not guaranteed to catch every peak above the threshold level,
    because it cannot apply gain reduction instantaneously - the time delay is
    determined by the attack time. However setting the attack time too short will
    distort the sound, so it is a compromise. High level peaks can be avoided by
    using a short attack time - but not too short, and setting the threshold a few
    decibels below the critical level.
    <br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterBool
    DSP::getParameterBool
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_COMPRESSOR_THRESHOLD,    /* (Type:float) - Threshold level (dB) in the range from -80 through 0.  Default = 0. */ 
    FMOD_DSP_COMPRESSOR_RATIO,        /* (Type:float) - Compression Ratio (dB/dB) in the range from 1 to 50.  Default = 2.5. */ 
    FMOD_DSP_COMPRESSOR_ATTACK,       /* (Type:float) - Attack time (milliseconds), in the range from 0.1 through 1000. Default value is 20. */
    FMOD_DSP_COMPRESSOR_RELEASE,      /* (Type:float) - Release time (milliseconds), in the range from 10 through 5000. Default value is 100 */
    FMOD_DSP_COMPRESSOR_GAINMAKEUP,   /* (Type:float) - Make-up gain (dB) applied after limiting, in the range from 0 through 30.  Default = 0. */
    FMOD_DSP_COMPRESSOR_USESIDECHAIN, /* (Type:data)  - Data of type FMOD_DSP_PARAMETER_SIDECHAIN. Whether to analyse the sidechain signal instead of the input signal. Default is { false } */
    FMOD_DSP_COMPRESSOR_LINKED        /* (Type:bool)  - FALSE = Independent (compressor per channel), TRUE = Linked.  Default = TRUE. */
} FMOD_DSP_COMPRESSOR;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_SFXREVERB unit.<br>
    
    [REMARKS]
    This is a high quality I3DL2 based reverb.<br>
    On top of the I3DL2 property set, "Dry Level" is also included to allow the dry mix to be changed.<br>
    <br>
    These properties can be set with presets in FMOD_REVERB_PRESETS.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
    FMOD_REVERB_PRESETS
]
*/
typedef enum
{
    FMOD_DSP_SFXREVERB_DECAYTIME,           /* (Type:float) - Decay Time       : Reverberation decay time at low-frequencies in milliseconds.  Ranges from 100.0 to 20000.0. Default is 1500. */
    FMOD_DSP_SFXREVERB_EARLYDELAY,          /* (Type:float) - Early Delay      : Delay time of first reflection in milliseconds.  Ranges from 0.0 to 300.0.  Default is 20. */
    FMOD_DSP_SFXREVERB_LATEDELAY,           /* (Type:float) - Reverb Delay     : Late reverberation delay time relative to first reflection in milliseconds.  Ranges from 0.0 to 100.0.  Default is 40. */
    FMOD_DSP_SFXREVERB_HFREFERENCE,         /* (Type:float) - HF Reference     : Reference frequency for high-frequency decay in Hz.  Ranges from 20.0 to 20000.0. Default is 5000. */
    FMOD_DSP_SFXREVERB_HFDECAYRATIO,        /* (Type:float) - Decay HF Ratio   : High-frequency decay time relative to decay time in percent.  Ranges from 10.0 to 100.0. Default is 50. */
    FMOD_DSP_SFXREVERB_DIFFUSION,           /* (Type:float) - Diffusion        : Reverberation diffusion (echo density) in percent.  Ranges from 0.0 to 100.0.  Default is 100. */
    FMOD_DSP_SFXREVERB_DENSITY,             /* (Type:float) - Density          : Reverberation density (modal density) in percent.  Ranges from 0.0 to 100.0.  Default is 100. */
    FMOD_DSP_SFXREVERB_LOWSHELFFREQUENCY,   /* (Type:float) - Low Shelf Frequency : Transition frequency of low-shelf filter in Hz.  Ranges from 20.0 to 1000.0. Default is 250. */
    FMOD_DSP_SFXREVERB_LOWSHELFGAIN,        /* (Type:float) - Low Shelf Gain   : Gain of low-shelf filter in dB.  Ranges from -36.0 to 12.0.  Default is 0. */
    FMOD_DSP_SFXREVERB_HIGHCUT,             /* (Type:float) - High Cut         : Cutoff frequency of low-pass filter in Hz.  Ranges from 20.0 to 20000.0. Default is 20000. */
    FMOD_DSP_SFXREVERB_EARLYLATEMIX,        /* (Type:float) - Early/Late Mix   : Blend ratio of late reverb to early reflections in percent.  Ranges from 0.0 to 100.0.  Default is 50. */
    FMOD_DSP_SFXREVERB_WETLEVEL,            /* (Type:float) - Wet Level        : Reverb signal level in dB.  Ranges from -80.0 to 20.0.  Default is -6. */
    FMOD_DSP_SFXREVERB_DRYLEVEL             /* (Type:float) - Dry Level        : Dry signal level in dB.  Ranges from -80.0 to 20.0.  Default is 0. */
} FMOD_DSP_SFXREVERB;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_LOWPASS_SIMPLE filter.

    [REMARKS]
    Deprecated and will be removed in a future release, to emulate with FMOD_DSP_TYPE_MULTIBAND_EQ:

        //  Configure a single band (band A) as a lowpass (all other bands default to off).
        //  12dB rolloff to approximate the old effect curve.
        //  Cutoff frequency can be used the same as with the old effect.
        //  Resonance / 'Q' should remain at default 0.707.
        FMOD_DSP_SetParameterInt(multiband, FMOD_DSP_MULTIBAND_EQ_A_FILTER, FMOD_DSP_MULTIBAND_EQ_FILTER_LOWPASS_12DB);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, frequency);

    This is a very simple low pass filter, based on two single-pole RC time-constant modules.

    The emphasis is on speed rather than accuracy, so this should not be used for task requiring critical filtering.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_LOWPASS_SIMPLE_CUTOFF     /* (Type:float) - Lowpass cutoff frequency in hz.  10.0 to 22000.0.  Default = 5000.0 */
} FMOD_DSP_LOWPASS_SIMPLE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_DELAY filter.

    [REMARKS]
    Note.  Every time MaxDelay is changed, the plugin re-allocates the delay buffer.  This means the delay will dissapear at that time while it refills its new buffer.<br>
    A larger MaxDelay results in larger amounts of memory allocated.<br>
    Channel delays above MaxDelay will be clipped to MaxDelay and the delay buffer will not be resized.<br>
    <br>
    <b>NOTE!</b> Not supported on PlayStation 3.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_DELAY_CH0,      /* (Type:float) - Channel  #0 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH1,      /* (Type:float) - Channel  #1 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH2,      /* (Type:float) - Channel  #2 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH3,      /* (Type:float) - Channel  #3 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH4,      /* (Type:float) - Channel  #4 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH5,      /* (Type:float) - Channel  #5 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH6,      /* (Type:float) - Channel  #6 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH7,      /* (Type:float) - Channel  #7 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH8,      /* (Type:float) - Channel  #8 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH9,      /* (Type:float) - Channel  #9 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH10,     /* (Type:float) - Channel #10 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH11,     /* (Type:float) - Channel #11 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH12,     /* (Type:float) - Channel #12 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH13,     /* (Type:float) - Channel #13 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH14,     /* (Type:float) - Channel #14 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_CH15,     /* (Type:float) - Channel #15 Delay in ms.  0  to 10000.  Default = 0. */
    FMOD_DSP_DELAY_MAXDELAY  /* (Type:float) - Maximum delay in ms.      0  to 10000.  Default = 10. */
} FMOD_DSP_DELAY;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_TREMOLO filter.

    [REMARKS]
    The tremolo effect varies the amplitude of a sound. Depending on the settings, this unit can produce a tremolo, chopper or auto-pan effect.<br>
    <br>
    The shape of the LFO (low freq. oscillator) can morphed between sine, triangle and sawtooth waves using the FMOD_DSP_TREMOLO_SHAPE and FMOD_DSP_TREMOLO_SKEW parameters.<br>
    FMOD_DSP_TREMOLO_DUTY and FMOD_DSP_TREMOLO_SQUARE are useful for a chopper-type effect where the first controls the on-time duration and second controls the flatness of the envelope.<br>
    FMOD_DSP_TREMOLO_SPREAD varies the LFO phase between channels to get an auto-pan effect. This works best with a sine shape LFO.<br>
    The LFO can be synchronized using the FMOD_DSP_TREMOLO_PHASE parameter which sets its instantaneous phase.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_TREMOLO_FREQUENCY,     /* (Type:float) - LFO frequency in Hz.  0.1 to 20.  Default = 5. */
    FMOD_DSP_TREMOLO_DEPTH,         /* (Type:float) - Tremolo depth.  0 to 1.  Default = 1. */
    FMOD_DSP_TREMOLO_SHAPE,         /* (Type:float) - LFO shape morph between triangle and sine.  0 to 1.  Default = 0. */
    FMOD_DSP_TREMOLO_SKEW,          /* (Type:float) - Time-skewing of LFO cycle.  -1 to 1.  Default = 0. */
    FMOD_DSP_TREMOLO_DUTY,          /* (Type:float) - LFO on-time.  0 to 1.  Default = 0.5. */
    FMOD_DSP_TREMOLO_SQUARE,        /* (Type:float) - Flatness of the LFO shape.  0 to 1.  Default = 0. */
    FMOD_DSP_TREMOLO_PHASE,         /* (Type:float) - Instantaneous LFO phase.  0 to 1.  Default = 0. */
    FMOD_DSP_TREMOLO_SPREAD         /* (Type:float) - Rotation / auto-pan effect.  -1 to 1.  Default = 0. */
} FMOD_DSP_TREMOLO;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_SEND DSP.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_SEND_RETURNID,     /* (Type:int) - ID of the Return DSP this send is connected to (integer values only). -1 indicates no connected Return DSP. Default = -1. */
    FMOD_DSP_SEND_LEVEL,        /* (Type:float) - Send level. 0.0 to 1.0. Default = 1.0 */
} FMOD_DSP_SEND;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_RETURN DSP.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_RETURN_ID,                /* (Type:int) - [r]   ID of this Return DSP. Read-only.  Default = -1. */
    FMOD_DSP_RETURN_INPUT_SPEAKER_MODE /* (Type:int) - [r/w] Input speaker mode of this return.  Default = FMOD_SPEAKERMODE_DEFAULT. */
} FMOD_DSP_RETURN;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_HIGHPASS_SIMPLE filter.

    [REMARKS]
    Deprecated and will be removed in a future release, to emulate with FMOD_DSP_TYPE_MULTIBAND_EQ:

        // Configure a single band (band A) as a highpass (all other bands default to off).
        // 12dB rolloff to approximate the old effect curve.
        // Cutoff frequency can be used the same as with the old effect.
        // Resonance / 'Q' should remain at default 0.707.
        FMOD_DSP_SetParameterInt(multiband, FMOD_DSP_MULTIBAND_EQ_A_FILTER, FMOD_DSP_MULTIBAND_EQ_FILTER_HIGHPASS_12DB);
        FMOD_DSP_SetParameterFloat(multiband, FMOD_DSP_MULTIBAND_EQ_A_FREQUENCY, frequency);

    This is a very simple single-order high pass filter.

    The emphasis is on speed rather than accuracy, so this should not be used for task requiring critical filtering.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_HIGHPASS_SIMPLE_CUTOFF     /* (Type:float) - Highpass cutoff frequency in hz.  10.0 to 22000.0.  Default = 1000.0 */
} FMOD_DSP_HIGHPASS_SIMPLE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter values for the FMOD_DSP_PAN_2D_STEREO_MODE parameter of the FMOD_DSP_TYPE_PAN DSP.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PAN
]
*/
typedef enum
{
    FMOD_DSP_PAN_2D_STEREO_MODE_DISTRIBUTED,     /* The parts of a stereo sound are spread around desination speakers based on FMOD_DSP_PAN_2D_EXTENT / FMOD_DSP_PAN_2D_DIRECTION */
    FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE         /* The L/R parts of a stereo sound are rotated around a circle based on FMOD_DSP_PAN_2D_STEREO_AXIS / FMOD_DSP_PAN_2D_STEREO_SEPARATION. */
} FMOD_DSP_PAN_2D_STEREO_MODE_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter values for the FMOD_DSP_PAN_MODE parameter of the FMOD_DSP_TYPE_PAN DSP.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PAN
]
*/
typedef enum
{
    FMOD_DSP_PAN_MODE_MONO,
    FMOD_DSP_PAN_MODE_STEREO,
    FMOD_DSP_PAN_MODE_SURROUND
} FMOD_DSP_PAN_MODE_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter values for the FMOD_DSP_PAN_3D_ROLLOFF parameter of the FMOD_DSP_TYPE_PAN DSP.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PAN
]
*/
typedef enum
{
    FMOD_DSP_PAN_3D_ROLLOFF_LINEARSQUARED,
    FMOD_DSP_PAN_3D_ROLLOFF_LINEAR,
    FMOD_DSP_PAN_3D_ROLLOFF_INVERSE,
    FMOD_DSP_PAN_3D_ROLLOFF_INVERSETAPERED,
    FMOD_DSP_PAN_3D_ROLLOFF_CUSTOM
} FMOD_DSP_PAN_3D_ROLLOFF_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter values for the FMOD_DSP_PAN_3D_EXTENT_MODE parameter of the FMOD_DSP_TYPE_PAN DSP.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PAN
]
*/
typedef enum
{
    FMOD_DSP_PAN_3D_EXTENT_MODE_AUTO,
    FMOD_DSP_PAN_3D_EXTENT_MODE_USER,
    FMOD_DSP_PAN_3D_EXTENT_MODE_OFF
} FMOD_DSP_PAN_3D_EXTENT_MODE_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_PAN DSP.

    [REMARKS]
    FMOD_DSP_PAN_3D_PAN_BLEND controls the percentage of the effect supplied by FMOD_DSP_PAN_2D_DIRECTION and FMOD_DSP_PAN_2D_EXTENT.

    For FMOD_DSP_PAN_3D_POSITION, the following members in the FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI struct should be non zero.
    - numlisteners                   - This is typically 1, can be up to 8.  Typically more than 1 is only used for split screen purposes.  The FMOD Panner will average angles and produce the best compromise for panning and attenuation.
    - relative[listenernum].position - This is the delta between the listener position and the sound position.  Typically the listener position is subtracted from the sound position.
    - relative[listenernum].forward  - This is the sound's forward vector.  Optional, set to 0,0,1 if not needed.  This is only relevant for more than mono sounds in 3D, that are spread amongst the destination speakers at the time of panning.   
                                       If the sound rotates then the L/R part of a stereo sound will rotate amongst its destination speakers.
                                       If the sound has moved and pinpointed into a single speaker, rotation of the sound will have no effect as at that point the channels are collapsed into a single point.

    For FMOD_DSP_PAN_2D_STEREO_MODE, when it is set to FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE, only FMOD_DSP_PAN_2D_STEREO_SEPARATION and FMOD_DSP_PAN_2D_STEREO_AXIS are used.
    When it is set to FMOD_DSP_PAN_2D_STEREO_MODE_DISTRIBUTED, then standard FMOD_DSP_PAN_2D_DIRECTION/FMOD_DSP_PAN_2D_EXTENT parameters are used.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_PAN_MODE,                          /* (Type:int)   - Panner mode.               FMOD_DSP_PAN_MODE_MONO for mono down-mix, FMOD_DSP_PAN_MODE_STEREO for stereo panning or FMOD_DSP_PAN_MODE_SURROUND for surround panning.  Default = FMOD_DSP_PAN_MODE_SURROUND */
    FMOD_DSP_PAN_2D_STEREO_POSITION,            /* (Type:float) - 2D Stereo pan position.    -100.0 to 100.0.  Default = 0.0. */
    FMOD_DSP_PAN_2D_DIRECTION,                  /* (Type:float) - 2D Surround pan direction. Direction from center point of panning circle. -180.0 (degrees) to 180.0 (degrees).  0 = front center, -180 or +180 = rear speakers center point. Default = 0.0. */
    FMOD_DSP_PAN_2D_EXTENT,                     /* (Type:float) - 2D Surround pan extent.    Distance from center point of panning circle.  0.0 (degrees) to 360.0 (degrees).  Default = 360.0. */
    FMOD_DSP_PAN_2D_ROTATION,                   /* (Type:float) - 2D Surround pan rotation.  -180.0 (degrees) to 180.0 (degrees).  Default = 0.0. */
    FMOD_DSP_PAN_2D_LFE_LEVEL,                  /* (Type:float) - 2D Surround pan LFE level. 2D LFE level in dB.  -80.0 (db) to 20.0 (db).  Default = 0.0. */
    FMOD_DSP_PAN_2D_STEREO_MODE,                /* (Type:int)   - Stereo-To-Surround Mode.   FMOD_DSP_PAN_2D_STEREO_MODE_DISTRIBUTED to FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE.  Default = FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE.*/
    FMOD_DSP_PAN_2D_STEREO_SEPARATION,          /* (Type:float) - Stereo-To-Surround Stereo  For FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE mode.  Separation/width of L/R parts of stereo sound. -180.0 (degrees) to +180.0 (degrees).  Default = 60.0. */
    FMOD_DSP_PAN_2D_STEREO_AXIS,                /* (Type:float) - Stereo-To-Surround Stereo  For FMOD_DSP_PAN_2D_STEREO_MODE_DISCRETE mode.  Axis/rotation of L/R parts of stereo sound. -180.0 (degrees) to +180.0 (degrees).  Default = 0.0. */
    FMOD_DSP_PAN_ENABLED_SPEAKERS,              /* (Type:int)   - Speakers Enabled.          Bitmask for each speaker from 0 to 32 to be considered by panner.  Use to disable speakers from being panned to.  0 to 0xFFF.  Default = 0xFFF (All on).  */
    FMOD_DSP_PAN_3D_POSITION,                   /* (Type:data)  - 3D Position.               Data of type FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI.  See remarks on what to fill out. */
    FMOD_DSP_PAN_3D_ROLLOFF,                    /* (Type:int)   - 3D Rolloff.                FMOD_DSP_PAN_3D_ROLLOFF_LINEARSQUARED to FMOD_DSP_PAN_3D_ROLLOFF_CUSTOM.  Default = FMOD_DSP_PAN_3D_ROLLOFF_LINEARSQUARED. */
    FMOD_DSP_PAN_3D_MIN_DISTANCE,               /* (Type:float) - 3D Min Distance.           0.0 to 1e+18f.  Default = 1.0. */
    FMOD_DSP_PAN_3D_MAX_DISTANCE,               /* (Type:float) - 3D Max Distance.           0.0 to 1e+18f.  Default = 20.0. */
    FMOD_DSP_PAN_3D_EXTENT_MODE,                /* (Type:int)   - 3D Extent Mode.            FMOD_DSP_PAN_3D_EXTENT_MODE_AUTO to FMOD_DSP_PAN_3D_EXTENT_MODE_OFF.  Default = FMOD_DSP_PAN_3D_EXTENT_MODE_AUTO. */
    FMOD_DSP_PAN_3D_SOUND_SIZE,                 /* (Type:float) - 3D Sound Size.             0.0 to 1e+18f.  Default = 0.0. */
    FMOD_DSP_PAN_3D_MIN_EXTENT,                 /* (Type:float) - 3D Min Extent.             0.0 (degrees) to 360.0 (degrees).  Default = 0.0. */
    FMOD_DSP_PAN_3D_PAN_BLEND,                  /* (Type:float) - 3D Pan Blend.              0.0 (fully 2D) to 1.0 (fully 3D).  Default = 0.0. */
    FMOD_DSP_PAN_LFE_UPMIX_ENABLED,             /* (Type:int)   - LFE Upmix Enabled.         Determines whether non-LFE source channels should mix to the LFE or leave it alone.  0 (off) to 1 (on).  Default = 0 (off). */
    FMOD_DSP_PAN_OVERALL_GAIN,                  /* (Type:data)  - Overall gain.              For information only, not set by user.  Data of type FMOD_DSP_PARAMETER_DATA_TYPE_OVERALLGAIN to provide to FMOD, to allow FMOD to know the DSP is scaling the signal for virtualization purposes. */
    FMOD_DSP_PAN_SURROUND_SPEAKER_MODE          /* (Type:int)   - Surround speaker mode.     Target speaker mode for surround panning.  Default = FMOD_SPEAKERMODE_DEFAULT. */
} FMOD_DSP_PAN;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter values for the FMOD_DSP_THREE_EQ_CROSSOVERSLOPE parameter of the FMOD_DSP_TYPE_THREE_EQ DSP.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_THREE_EQ
]
*/
typedef enum
{
    FMOD_DSP_THREE_EQ_CROSSOVERSLOPE_12DB,
    FMOD_DSP_THREE_EQ_CROSSOVERSLOPE_24DB,
    FMOD_DSP_THREE_EQ_CROSSOVERSLOPE_48DB
} FMOD_DSP_THREE_EQ_CROSSOVERSLOPE_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_THREE_EQ filter.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    FMOD_DSP_TYPE
    FMOD_DSP_THREE_EQ_CROSSOVERSLOPE_TYPE
]
*/
typedef enum
{
    FMOD_DSP_THREE_EQ_LOWGAIN,       /* (Type:float) - Low frequency gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_THREE_EQ_MIDGAIN,       /* (Type:float) - Mid frequency gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_THREE_EQ_HIGHGAIN,      /* (Type:float) - High frequency gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_THREE_EQ_LOWCROSSOVER,  /* (Type:float) - Low-to-mid crossover frequency in Hz.  10.0 to 22000.0.  Default = 400.0. */
    FMOD_DSP_THREE_EQ_HIGHCROSSOVER, /* (Type:float) - Mid-to-high crossover frequency in Hz.  10.0 to 22000.0.  Default = 4000.0. */
    FMOD_DSP_THREE_EQ_CROSSOVERSLOPE /* (Type:int)   - Crossover Slope.  0 = 12dB/Octave, 1 = 24dB/Octave, 2 = 48dB/Octave.  Default = 1 (24dB/Octave). */
} FMOD_DSP_THREE_EQ;


/*
[ENUM]
[
    [DESCRIPTION]
    List of windowing methods for the FMOD_DSP_TYPE_FFT unit.  Used in spectrum analysis to reduce leakage / transient signals intefering with the analysis.<br>
    This is a problem with analysis of continuous signals that only have a small portion of the signal sample (the fft window size).<br>
    Windowing the signal with a curve or triangle tapers the sides of the fft window to help alleviate this problem.

    [REMARKS]
    Cyclic signals such as a sine wave that repeat their cycle in a multiple of the window size do not need windowing.<br>
    I.e. If the sine wave repeats every 1024, 512, 256 etc samples and the FMOD fft window is 1024, then the signal would not need windowing.<br>
    Not windowing is the same as FMOD_DSP_FFT_WINDOW_RECT, which is the default.<br>
    If the cycle of the signal (ie the sine wave) is not a multiple of the window size, it will cause frequency abnormalities, so a different windowing method is needed.<br>
    <exclude>
    <br>
    FMOD_DSP_FFT_WINDOW_RECT.<br>
    <img src="..\static\overview\rectangle.gif"></img><br>
    <br>
    FMOD_DSP_FFT_WINDOW_TRIANGLE.<br>
    <img src="..\static\overview\triangle.gif"></img><br>
    <br>
    FMOD_DSP_FFT_WINDOW_HAMMING.<br>
    <img src="..\static\overview\hamming.gif"></img><br>
    <br>
    FMOD_DSP_FFT_WINDOW_HANNING.<br>
    <img src="..\static\overview\hanning.gif"></img><br>
    <br>
    FMOD_DSP_FFT_WINDOW_BLACKMAN.<br>
    <img src="..\static\overview\blackman.gif"></img><br>
    <br>
    FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS.<br>
    <img src="..\static\overview\blackmanharris.gif"></img>
    </exclude>
    
    [SEE_ALSO]
    FMOD_DSP_FFT
]
*/
typedef enum
{
    FMOD_DSP_FFT_WINDOW_RECT,            /* w[n] = 1.0                                                                                            */
    FMOD_DSP_FFT_WINDOW_TRIANGLE,        /* w[n] = TRI(2n/N)                                                                                      */
    FMOD_DSP_FFT_WINDOW_HAMMING,         /* w[n] = 0.54 - (0.46 * COS(n/N) )                                                                      */
    FMOD_DSP_FFT_WINDOW_HANNING,         /* w[n] = 0.5 *  (1.0  - COS(n/N) )                                                                      */
    FMOD_DSP_FFT_WINDOW_BLACKMAN,        /* w[n] = 0.42 - (0.5  * COS(n/N) ) + (0.08 * COS(2.0 * n/N) )                                           */
    FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS   /* w[n] = 0.35875 - (0.48829 * COS(1.0 * n/N)) + (0.14128 * COS(2.0 * n/N)) - (0.01168 * COS(3.0 * n/N)) */
} FMOD_DSP_FFT_WINDOW;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_FFT dsp effect.

    [REMARKS]
    Set the attributes for the spectrum analysis with FMOD_DSP_FFT_WINDOWSIZE and FMOD_DSP_FFT_WINDOWTYPE, and retrieve the results with FMOD_DSP_FFT_SPECTRUM and FMOD_DSP_FFT_DOMINANT_FREQ.
    FMOD_DSP_FFT_SPECTRUM stores its data in the FMOD_DSP_PARAMETER_DATA_TYPE_FFT.  You will need to cast to this structure to get the right data.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_TYPE
    FMOD_DSP_FFT_WINDOW
]
*/
typedef enum
{
    FMOD_DSP_FFT_WINDOWSIZE,            /*  (Type:int)   - [r/w] Must be a power of 2 between 128 and 16384.  128, 256, 512, 1024, 2048, 4096, 8192, 16384 are accepted.  Default = 2048. */
    FMOD_DSP_FFT_WINDOWTYPE,            /*  (Type:int)   - [r/w] Refer to FMOD_DSP_FFT_WINDOW enumeration.  Default = FMOD_DSP_FFT_WINDOW_HAMMING. */
    FMOD_DSP_FFT_SPECTRUMDATA,          /*  (Type:data)  - [r]   Returns the current spectrum values between 0 and 1 for each 'fft bin'.  Cast data to FMOD_DSP_PARAMETER_DATA_TYPE_FFT.  Divide the niquist rate by the window size to get the hz value per entry. */
    FMOD_DSP_FFT_DOMINANT_FREQ          /*  (Type:float) - [r]   Returns the dominant frequencies for each channel. */
} FMOD_DSP_FFT;


/*
[ENUM]
[  
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_ENVELOPEFOLLOWER unit.
    This is a simple envelope follower for tracking the signal level.<br>

    [REMARKS]
    This unit does not affect the incoming signal
    <br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_ENVELOPEFOLLOWER_ATTACK,      /* (Type:float) [r/w] - Attack time (milliseconds), in the range from 0.1 through 1000.  Default = 20. */
    FMOD_DSP_ENVELOPEFOLLOWER_RELEASE,     /* (Type:float) [r/w] - Release time (milliseconds), in the range from 10 through 5000.  Default = 100 */
    FMOD_DSP_ENVELOPEFOLLOWER_ENVELOPE,    /* (Type:float) [r]   - Current value of the envelope, in the range 0 to 1. Read-only. */
    FMOD_DSP_ENVELOPEFOLLOWER_USESIDECHAIN /* (Type:data)  [r/w] - Data of type FMOD_DSP_PARAMETER_SIDECHAIN. Whether to analyse the sidechain signal instead of the input signal. Default is { false } */
} FMOD_DSP_ENVELOPEFOLLOWER;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_CONVOLUTIONREVERB filter.

    [REMARKS]
    Convolution Reverb reverb IR.<br>

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_CONVOLUTION_REVERB_PARAM_IR,       /* (Type:data)  - [w]   16-bit reverb IR (short*) with an extra sample prepended to the start which specifies the number of channels. */
    FMOD_DSP_CONVOLUTION_REVERB_PARAM_WET,      /* (Type:float) - [r/w] Volume of echo signal to pass to output in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CONVOLUTION_REVERB_PARAM_DRY,      /* (Type:float) - [r/w] Original sound volume in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CONVOLUTION_REVERB_PARAM_LINKED    /* (Type:bool)  - [r/w] Linked - channels are mixed together before processing through the reverb.  Default = TRUE. */
} FMOD_DSP_CONVOLUTION_REVERB;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_CHANNELMIX_OUTPUTGROUPING parameter for FMOD_DSP_TYPE_CHANNELMIX effect.

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_CHANNELMIX_OUTPUT_DEFAULT,      /*  Output channel count = input channel count.  Mapping: See FMOD_SPEAKER enumeration. */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLMONO,      /*  Output channel count = 1.  Mapping: Mono, Mono, Mono, Mono, Mono, Mono, ... (each channel all the way up to FMOD_MAX_CHANNEL_WIDTH channels are treated as if they were mono) */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLSTEREO,    /*  Output channel count = 2.  Mapping: Left, Right, Left, Right, Left, Right, ... (each pair of channels is treated as stereo all the way up to FMOD_MAX_CHANNEL_WIDTH channels) */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLQUAD,      /*  Output channel count = 4.  Mapping: Repeating pattern of Front Left, Front Right, Surround Left, Surround Right. */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALL5POINT1,   /*  Output channel count = 6.  Mapping: Repeating pattern of Front Left, Front Right, Center, LFE, Surround Left, Surround Right. */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALL7POINT1,   /*  Output channel count = 8.  Mapping: Repeating pattern of Front Left, Front Right, Center, LFE, Surround Left, Surround Right, Back Left, Back Right.  */
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLLFE        /*  Output channel count = 6.  Mapping: Repeating pattern of LFE in a 5.1 output signal.  */
} FMOD_DSP_CHANNELMIX_OUTPUT;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_CHANNELMIX filter.

    [REMARKS]
    For FMOD_DSP_CHANNELMIX_OUTPUTGROUPING, this value will set the output speaker format for the DSP, and also map the incoming channels to the 
    outgoing channels in a round-robin fashion.  Use this for example play a 32 channel input signal as if it were a repeating group of output signals.
    Ie.
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLMONO    = all incoming channels are mixed to a mono output.
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLSTEREO  = all incoming channels are mixed to a stereo output, ie even incoming channels 0,2,4,6,etc are mixed to left, and odd incoming channels 1,3,5,7,etc are mixed to right.
    FMOD_DSP_CHANNELMIX_OUTPUT_ALL5POINT1 = all incoming channels are mixed to a 5.1 output.  If there are less than 6 coming in, it will just fill the first n channels in the 6 output channels.   
                                            If there are more, then it will repeat the input pattern to the output like it did with the stereo case, ie 12 incoming channels are mapped as 0-5 mixed to the 
                                            5.1 output and 6 to 11 mapped to the 5.1 output.
    FMOD_DSP_CHANNELMIX_OUTPUT_ALLLFE     = all incoming channels are mixed to a 5.1 output but via the LFE channel only.

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_CHANNELMIX_OUTPUTGROUPING,     /* (Type:int)   - Refer to FMOD_DSP_CHANNELMIX_OUTPUT enumeration.  Default = FMOD_DSP_CHANNELMIX_OUTPUT_DEFAULT.  See remarks. */
    FMOD_DSP_CHANNELMIX_GAIN_CH0,           /* (Type:float) - Channel  #0 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH1,           /* (Type:float) - Channel  #1 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH2,           /* (Type:float) - Channel  #2 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH3,           /* (Type:float) - Channel  #3 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH4,           /* (Type:float) - Channel  #4 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH5,           /* (Type:float) - Channel  #5 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH6,           /* (Type:float) - Channel  #6 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH7,           /* (Type:float) - Channel  #7 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH8,           /* (Type:float) - Channel  #8 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH9,           /* (Type:float) - Channel  #9 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH10,          /* (Type:float) - Channel #10 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH11,          /* (Type:float) - Channel #11 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH12,          /* (Type:float) - Channel #12 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH13,          /* (Type:float) - Channel #13 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH14,          /* (Type:float) - Channel #14 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH15,          /* (Type:float) - Channel #15 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH16,          /* (Type:float) - Channel #16 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH17,          /* (Type:float) - Channel #17 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH18,          /* (Type:float) - Channel #18 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH19,          /* (Type:float) - Channel #19 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH20,          /* (Type:float) - Channel #20 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH21,          /* (Type:float) - Channel #21 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH22,          /* (Type:float) - Channel #22 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH23,          /* (Type:float) - Channel #23 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH24,          /* (Type:float) - Channel #24 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH25,          /* (Type:float) - Channel #25 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH26,          /* (Type:float) - Channel #26 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH27,          /* (Type:float) - Channel #27 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH28,          /* (Type:float) - Channel #28 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH29,          /* (Type:float) - Channel #29 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH30,          /* (Type:float) - Channel #30 gain in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_CHANNELMIX_GAIN_CH31           /* (Type:float) - Channel #31 gain in dB.  -80.0 to 10.0.  Default = 0. */
} FMOD_DSP_CHANNELMIX;

/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TRANSCEIVER_SPEAKERMODE parameter for FMOD_DSP_TYPE_TRANSCEIVER effect.

    [REMARKS]
    The speaker mode of a transceiver buffer (of which there are up to 32 of) is determined automatically depending on the signal flowing through the transceiver effect, or it can be forced.
    Use a smaller fixed speaker mode buffer to save memory.

    Only relevant for transmitter dsps, as they control the format of the transceiver channel's buffer.

    If multiple transceivers transmit to a single buffer in different speaker modes, it will allocate memory for each speaker mode.   This uses more memory than a single speaker mode.
    If there are multiple receivers reading from a channel with multiple speaker modes, it will read them all and mix them together.

    If the system's speaker mode is stereo or mono, it will not create a 3rd buffer, it will just use the mono/stereo speaker mode buffer.

    [SEE_ALSO]
    DSP::setParameterInt
    DSP::getParameterInt
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_TRANSCEIVER_SPEAKERMODE_AUTO = -1,     /* A transmitter will use whatever signal channel count coming in to the transmitter, to determine which speaker mode is allocated for the transceiver channel. */
    FMOD_DSP_TRANSCEIVER_SPEAKERMODE_MONO = 0,      /* A transmitter will always downmix to a mono channel buffer. */
    FMOD_DSP_TRANSCEIVER_SPEAKERMODE_STEREO,        /* A transmitter will always upmix or downmix to a stereo channel buffer. */
    FMOD_DSP_TRANSCEIVER_SPEAKERMODE_SURROUND,      /* A transmitter will always upmix or downmix to a surround channel buffer.   Surround is the speaker mode of the system above stereo, so could be quad/surround/5.1/7.1. */
} FMOD_DSP_TRANSCEIVER_SPEAKERMODE;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_TRANSCEIVER filter.

    [REMARKS]
    The transceiver only transmits and receives to a global array of 32 channels.   The transceiver can be set to receiver mode (like a return) and can receive the signal at a variable gain (FMOD_DSP_TRANSCEIVER_GAIN).
    The transceiver can also be set to transmit to a chnnel (like a send) and can transmit the signal with a variable gain (FMOD_DSP_TRANSCEIVER_GAIN).
    
    The FMOD_DSP_TRANSCEIVER_TRANSMITSPEAKERMODE is only applicable to the transmission format, not the receive format.   This means this parameter is ignored in 'receive mode'.  This allows receivers to receive at
    the speaker mode of the user's choice.   Receiving from a mono channel, is cheaper than receiving from a surround channel for example.
    The 3 speaker modes FMOD_DSP_TRANSCEIVER_SPEAKERMODE_MONO, FMOD_DSP_TRANSCEIVER_SPEAKERMODE_STEREO, FMOD_DSP_TRANSCEIVER_SPEAKERMODE_SURROUND are stored as seperate buffers in memory for a tranmitter channel.
    To save memory, use 1 common speaker mode for a transmitter.

    The transceiver is double buffered to avoid desyncing of transmitters and receivers.   This means there will be a 1 block delay on a receiver, compared to the data sent from a transmitter.

    Multiple transmitters sending to the same channel will be mixed together.

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterBool
    DSP::getParameterBool
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_TRANSCEIVER_TRANSMIT,            /* (Type:bool)  - [r/w] - FALSE = Transceiver is a 'receiver' (like a return) and accepts data from a channel.  TRUE = Transceiver is a 'transmitter' (like a send).  Default = FALSE. */
    FMOD_DSP_TRANSCEIVER_GAIN,                /* (Type:float) - [r/w] - Gain to receive or transmit at in dB.  -80.0 to 10.0.  Default = 0. */
    FMOD_DSP_TRANSCEIVER_CHANNEL,             /* (Type:int)   - [r/w] - Integer to select current global slot, shared by all Transceivers, that can be transmitted to or received from.  0 to 31.  Default = 0.*/
    FMOD_DSP_TRANSCEIVER_TRANSMITSPEAKERMODE  /* (Type:int)   - [r/w] - Speaker mode (transmitter mode only).  Specifies either 0 (Auto) Default = 0.*/
} FMOD_DSP_TRANSCEIVER;


/*
[ENUM]
[
    [DESCRIPTION]
    Parameter types for the FMOD_DSP_TYPE_OBJECTPAN DSP.  3D Object panners are meant for hardware 3d object systems like Dolby Atmos or Sony Morpheus.
    These object panners take input in, and send it to the 7.1 bed, but do not send the signal further down the DSP chain (the output of the dsp is silence).

    [REMARKS]

    [SEE_ALSO]
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_TYPE
]
*/
typedef enum
{
    FMOD_DSP_OBJECTPAN_3D_POSITION,                   /* (Type:data)  - 3D Position.               data of type FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI */
    FMOD_DSP_OBJECTPAN_3D_ROLLOFF,                    /* (Type:int)   - 3D Rolloff.                FMOD_DSP_PAN_3D_ROLLOFF_LINEARSQUARED to FMOD_DSP_PAN_3D_ROLLOFF_CUSTOM.  Default = FMOD_DSP_PAN_3D_ROLLOFF_LINEARSQUARED. */
    FMOD_DSP_OBJECTPAN_3D_MIN_DISTANCE,               /* (Type:float) - 3D Min Distance.           0.0 to 1e+18f.  Default = 1.0. */
    FMOD_DSP_OBJECTPAN_3D_MAX_DISTANCE,               /* (Type:float) - 3D Max Distance.           0.0 to 1e+18f.  Default = 20.0. */
    FMOD_DSP_OBJECTPAN_3D_EXTENT_MODE,                /* (Type:int)   - 3D Extent Mode.            FMOD_DSP_PAN_3D_EXTENT_MODE_AUTO to FMOD_DSP_PAN_3D_EXTENT_MODE_OFF.  Default = FMOD_DSP_PAN_3D_EXTENT_MODE_AUTO. */
    FMOD_DSP_OBJECTPAN_3D_SOUND_SIZE,                 /* (Type:float) - 3D Sound Size.             0.0 to 1e+18f.  Default = 0.0. */
    FMOD_DSP_OBJECTPAN_3D_MIN_EXTENT,                 /* (Type:float) - 3D Min Extent.             0.0 (degrees) to 360.0 (degrees).  Default = 0.0. */
    FMOD_DSP_OBJECTPAN_OVERALL_GAIN,                  /* (Type:data)  - Overall gain.              For information only, not set by user.  Data of type FMOD_DSP_PARAMETER_DATA_TYPE_OVERALLGAIN to provide to FMOD, to allow FMOD to know the DSP is scaling the signal for virtualization purposes. */
    FMOD_DSP_OBJECTPAN_OUTPUTGAIN                     /* (Type:float) - Output gain level.         0.0 to 1.0 linear scale.  For the user to scale the output of the object panner's signal. */
} FMOD_DSP_OBJECTPAN;

#endif

