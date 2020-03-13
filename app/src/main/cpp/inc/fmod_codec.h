/* ======================================================================================================== */
/* FMOD Studio - codec development header file. Copyright (c), Firelight Technologies Pty, Ltd. 2004-2017.  */
/*                                                                                                          */
/* Use this header if you are wanting to develop your own file format plugin to use with                    */
/* FMOD's codec system.  With this header you can make your own fileformat plugin that FMOD                 */
/* can register and use.  See the documentation and examples on how to make a working plugin.               */
/*                                                                                                          */
/* ======================================================================================================== */

#ifndef _FMOD_CODEC_H
#define _FMOD_CODEC_H

typedef struct FMOD_CODEC_STATE FMOD_CODEC_STATE;
typedef struct FMOD_CODEC_WAVEFORMAT FMOD_CODEC_WAVEFORMAT;

/*
    Codec callbacks
*/ 
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_OPEN_CALLBACK)         (FMOD_CODEC_STATE *codec_state, FMOD_MODE usermode, FMOD_CREATESOUNDEXINFO *userexinfo);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_CLOSE_CALLBACK)        (FMOD_CODEC_STATE *codec_state);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_READ_CALLBACK)         (FMOD_CODEC_STATE *codec_state, void *buffer, unsigned int samples_in, unsigned int *samples_out);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_GETLENGTH_CALLBACK)    (FMOD_CODEC_STATE *codec_state, unsigned int *length, FMOD_TIMEUNIT lengthtype);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_SETPOSITION_CALLBACK)  (FMOD_CODEC_STATE *codec_state, int subsound, unsigned int position, FMOD_TIMEUNIT postype);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_GETPOSITION_CALLBACK)  (FMOD_CODEC_STATE *codec_state, unsigned int *position, FMOD_TIMEUNIT postype);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_SOUNDCREATE_CALLBACK)  (FMOD_CODEC_STATE *codec_state, int subsound, FMOD_SOUND *sound);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_METADATA_CALLBACK)     (FMOD_CODEC_STATE *codec_state, FMOD_TAGTYPE tagtype, char *name, void *data, unsigned int datalen, FMOD_TAGDATATYPE datatype, int unique);
typedef FMOD_RESULT (F_CALLBACK *FMOD_CODEC_GETWAVEFORMAT_CALLBACK)(FMOD_CODEC_STATE *codec_state, int index, FMOD_CODEC_WAVEFORMAT *waveformat);


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    When creating a codec, declare one of these and provide the relevant callbacks and name for FMOD to use when it opens and reads a file.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]
    FMOD_CODEC_STATE
    FMOD_CODEC_WAVEFORMAT
]
*/
typedef struct FMOD_CODEC_DESCRIPTION
{
    const char                       *name;            /* [w] Name of the codec. */
    unsigned int                      version;         /* [w] Plugin writer's version number. */
    int                               defaultasstream; /* [w] Tells FMOD to open the file as a stream when calling System::createSound, and not a static sample.  Should normally be 0 (FALSE), because generally the user wants to decode the file into memory when using System::createSound.   Mainly used for formats that decode for a very long time, or could use large amounts of memory when decoded.  Usually sequenced formats such as mod/s3m/xm/it/midi fall into this category.   It is mainly to stop users that don't know what they're doing from getting FMOD_ERR_MEMORY returned from createSound when they should have in fact called System::createStream or used FMOD_CREATESTREAM in System::createSound. */
    FMOD_TIMEUNIT                     timeunits;       /* [w] When setposition codec is called, only these time formats will be passed to the codec. Use bitwise OR to accumulate different types. */
    FMOD_CODEC_OPEN_CALLBACK          open;            /* [w] Open callback for the codec for when FMOD tries to open a sound using this codec. */
    FMOD_CODEC_CLOSE_CALLBACK         close;           /* [w] Close callback for the codec for when FMOD tries to close a sound using this codec.  */
    FMOD_CODEC_READ_CALLBACK          read;            /* [w] Read callback for the codec for when FMOD tries to read some data from the file to the destination format (specified in the open callback). */
    FMOD_CODEC_GETLENGTH_CALLBACK     getlength;       /* [w] Callback to return the length of the song in whatever format required when Sound::getLength is called. */
    FMOD_CODEC_SETPOSITION_CALLBACK   setposition;     /* [w] Seek callback for the codec for when FMOD tries to seek within the file with Channel::setPosition. */
    FMOD_CODEC_GETPOSITION_CALLBACK   getposition;     /* [w] Tell callback for the codec for when FMOD tries to get the current position within the with Channel::getPosition. */
    FMOD_CODEC_SOUNDCREATE_CALLBACK   soundcreate;     /* [w] Sound creation callback for the codec when FMOD finishes creating the sound.  (So the codec can set more parameters for the related created sound, ie loop points/mode or 3D attributes etc). */
    FMOD_CODEC_GETWAVEFORMAT_CALLBACK getwaveformat;   /* [w] Callback to tell FMOD about the waveformat of a particular subsound.  This is to save memory, rather than saving 1000 FMOD_CODEC_WAVEFORMAT structures in the codec, the codec might have a more optimal way of storing this information. */
} FMOD_CODEC_DESCRIPTION;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Set these values marked to tell fmod what sort of sound to create when the codec open callback is called.<br>
    The format, channels, frequency and lengthpcm tell FMOD what sort of sound buffer to create when you initialize your code. <br>
    If you wrote an MP3 codec that decoded to stereo 16bit integer PCM for a 44khz sound, you would specify FMOD_SOUND_FORMAT_PCM16, and channels would be equal to 2, and frequency would be 44100.<br>

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>
    <br>
    1.07 Note.  'blockalign' member which was in bytes has been removed.   'pcmblocksize' is now the replacement, and is measured in PCM samples only, not bytes.  This is purely to support buffering 
    internal to FMOD for codecs that are not sample accurate.
    <br>
    Note: When registering a codec, format, channels, frequency and lengthpcm must be supplied, otherwise there will be an error.<br>
    This structure is optional if FMOD_CODEC_GETWAVEFORMAT_CALLBACK is specified.<br>
    An array of these structures may be needed if FMOD_CODEC_STATE::numsubsounds is larger than 1.
    
    
    [SEE_ALSO]
    FMOD_CODEC_STATE
    FMOD_SOUND_FORMAT
    FMOD_MODE
    FMOD_CHANNELMASK
    FMOD_CHANNELORDER
    FMOD_CODEC_WAVEFORMAT_VERSION
]
*/
struct FMOD_CODEC_WAVEFORMAT
{
    const char*        name;          /* [w] Name of sound.  Optional. If used, the codec must own the lifetime of the string memory until the codec is destroyed. */
    FMOD_SOUND_FORMAT  format;        /* [w] Format for (decompressed) codec output, ie FMOD_SOUND_FORMAT_PCM8, FMOD_SOUND_FORMAT_PCM16.  Mandantory - Must be supplied. */
    int                channels;      /* [w] Number of channels used by codec, ie mono = 1, stereo = 2.  Mandantory - Must be supplied.  */
    int                frequency;     /* [w] Default frequency in hz of the codec, ie 44100.  Mandantory - Must be supplied.  */
    unsigned int       lengthbytes;   /* [w] Length in bytes of the source data.  Used for FMOD_TIMEUNIT_RAWBYTES.  Optional. Default = 0. */
    unsigned int       lengthpcm;     /* [w] Length in decompressed, PCM samples of the file, ie length in seconds * frequency.  Used for Sound::getLength and for memory allocation of static decompressed sample data.  Mandantory - Must be supplied. */
    unsigned int       pcmblocksize;  /* [w] Minimum, optimal number of decompressed PCM samples codec can handle.  0 or 1 = no buffering.  Anything higher means FMOD will allocate a PCM buffer of this size to read in chunks.  The codec read callback will be called in multiples of this value.  Optional.  */
    int                loopstart;     /* [w] Loopstart in decompressed, PCM samples of file. Optional. Default = 0. */
    int                loopend;       /* [w] Loopend in decompressed, PCM samples of file. Optional. Default = 0. */
    FMOD_MODE          mode;          /* [w] Mode to determine whether the sound should by default load as looping, non looping, 2d or 3d.  Optional. Default = FMOD_DEFAULT. */
    FMOD_CHANNELMASK   channelmask;   /* [w] Defined channel bitmask to describe which speakers the channels in the codec map to, in order of channel count.  See fmod_common.h.  Optional. Leave at 0 to map to the speaker layout defined in FMOD_SPEAKER. */
    FMOD_CHANNELORDER  channelorder;  /* [w] Defined channel order type, to describe where each sound channel should pan for the number of channels specified.  See fmod_common.h.  Optional.  Leave at 0 to play in default speaker order. */
    float              peakvolume;    /* [w] Peak volume of sound. Optional. Default = 0 if not used. */
};


/*
[DEFINE] 
[
    [NAME]
    FMOD_CODEC_WAVEFORMAT_VERSION

    [DESCRIPTION]
    Version number of FMOD_CODEC_WAVEFORMAT structure.  Should be set into FMOD_CODEC_STATE in the FMOD_CODEC_OPEN_CALLBACK.

    [REMARKS]
    Use this for binary compatibility and for future expansion.

    [SEE_ALSO]
    FMOD_CODEC_STATE
    FMOD_CODEC_DESCRIPTION
    FMOD_CODEC_OPEN_CALLBACK
]
*/
#define FMOD_CODEC_WAVEFORMAT_VERSION 3
/* [DEFINE_END] */


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Codec plugin structure that is passed into each callback.<br>
    <br>
    Optionally set the numsubsounds and waveformat members when called in FMOD_CODEC_OPEN_CALLBACK to tell fmod what sort of sound to create.<br>

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>
    <br>
    'numsubsounds' should be 0 if the file is a normal single sound stream or sound.  Examples of this would be .WAV, .WMA, .MP3, .AIFF.<br>
    'numsubsounds' should be 1+ if the file is a container format, and does not contain wav data itself.  Examples of these types would be FSB (contains multiple sounds), DLS (contain instruments).<br>
    The waveformat value should point to an arrays of information based on how many subsounds are in the format.  If the number of subsounds is 0 then it should point to 1 waveformat, the same as if the number of subsounds was 1.  If subsounds was 100 for example, there should be a pointer to an array of 100 waveformat structures.<br>
    <br>
    The waveformat pointer is optional and could be 0, if using FMOD_CODEC_GETWAVEFORMAT_CALLBACK is preferred.<br>
    <br>
    When a sound has 1 or more subsounds, the caller must play the individual sounds specified by first obtaining the subsound with Sound::getSubSound.
    
    [SEE_ALSO]
    FMOD_CODEC_WAVEFORMAT
    FMOD_FILE_READ_CALLBACK
    FMOD_FILE_SEEK_CALLBACK
    FMOD_CODEC_METADATA_CALLBACK
    Sound::getSubSound
    Sound::getNumSubSounds
    FMOD_CODEC_WAVEFORMAT_VERSION
]
*/
struct FMOD_CODEC_STATE
{
    int                          numsubsounds;      /* [w] Number of 'subsounds' in this sound.  Anything other than 0 makes it a 'container' format (ie DLS/FSB etc which contain 1 or more subsounds).  For most normal, single sound codec such as WAV/AIFF/MP3, this should be 0 as they are not a container for subsounds, they are the sound by itself. */
    FMOD_CODEC_WAVEFORMAT       *waveformat;        /* [w] Pointer to an array of format structures containing information about each sample.  Can be 0 or NULL if FMOD_CODEC_GETWAVEFORMAT_CALLBACK callback is preferred.  The number of entries here must equal the number of subsounds defined in the subsound parameter. If numsubsounds = 0 then there should be 1 instance of this structure. */
    void                        *plugindata;        /* [w] Plugin writer created data the codec author wants to attach to this object. */
                                               
    void                        *filehandle;        /* [r] This will return an internal FMOD file handle to use with the callbacks provided.  */
    unsigned int                 filesize;          /* [r] This will contain the size of the file in bytes. */
    FMOD_FILE_READ_CALLBACK      fileread;          /* [r] This will return a callable FMOD file function to use from codec. */
    FMOD_FILE_SEEK_CALLBACK      fileseek;          /* [r] This will return a callable FMOD file function to use from codec.  */
    FMOD_CODEC_METADATA_CALLBACK metadata;          /* [r] This will return a callable FMOD metadata function to use from codec.  */

    int                          waveformatversion; /* [w] Must be set to FMOD_CODEC_WAVEFORMAT_VERSION in the FMOD_CODEC_OPEN_CALLBACK. */
};

#endif


