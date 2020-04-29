/* ======================================================================================================== */
/* FMOD Studio - output development header file. Copyright (c), Firelight Technologies Pty, Ltd. 2004-2017. */
/*                                                                                                          */
/* Use this header if you are wanting to develop your own output plugin to use with                         */
/* FMOD's output system.  With this header you can make your own output plugin that FMOD                    */
/* can register and use.  See the documentation and examples on how to make a working plugin.               */
/*                                                                                                          */
/* ======================================================================================================== */

#ifndef _FMOD_OUTPUT_H
#define _FMOD_OUTPUT_H

#define FMOD_OUTPUT_PLUGIN_VERSION 2

typedef struct FMOD_OUTPUT_STATE        FMOD_OUTPUT_STATE;
typedef struct FMOD_OUTPUT_OBJECT3DINFO FMOD_OUTPUT_OBJECT3DINFO;

/*
    FMOD_OUTPUT_DESCRIPTION callbacks
*/ 
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_GETNUMDRIVERS_CALLBACK)    (FMOD_OUTPUT_STATE *output_state, int *numdrivers);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_GETDRIVERINFO_CALLBACK)    (FMOD_OUTPUT_STATE *output_state, int id, char *name, int namelen, FMOD_GUID *guid, int *systemrate, FMOD_SPEAKERMODE *speakermode, int *speakermodechannels);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_INIT_CALLBACK)             (FMOD_OUTPUT_STATE *output_state, int selecteddriver, FMOD_INITFLAGS flags, int *outputrate, FMOD_SPEAKERMODE *speakermode, int *speakermodechannels, FMOD_SOUND_FORMAT *outputformat, int dspbufferlength, int dspnumbuffers, void *extradriverdata);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_START_CALLBACK)            (FMOD_OUTPUT_STATE *output_state);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_STOP_CALLBACK)             (FMOD_OUTPUT_STATE *output_state);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_CLOSE_CALLBACK)            (FMOD_OUTPUT_STATE *output_state);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_UPDATE_CALLBACK)           (FMOD_OUTPUT_STATE *output_state);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_GETHANDLE_CALLBACK)        (FMOD_OUTPUT_STATE *output_state, void **handle);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_GETPOSITION_CALLBACK)      (FMOD_OUTPUT_STATE *output_state, unsigned int *pcm);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_LOCK_CALLBACK)             (FMOD_OUTPUT_STATE *output_state, unsigned int offset, unsigned int length, void **ptr1, void **ptr2, unsigned int *len1, unsigned int *len2);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_UNLOCK_CALLBACK)           (FMOD_OUTPUT_STATE *output_state, void *ptr1, void *ptr2, unsigned int len1, unsigned int len2);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_MIXER_CALLBACK)            (FMOD_OUTPUT_STATE *output_state);

typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_OBJECT3DGETINFO_CALLBACK)  (FMOD_OUTPUT_STATE *output_state, int *maxhardwareobjects);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_OBJECT3DALLOC_CALLBACK)    (FMOD_OUTPUT_STATE *output_state, void **object3d);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_OBJECT3DFREE_CALLBACK)     (FMOD_OUTPUT_STATE *output_state, void *object3d);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_OBJECT3DUPDATE_CALLBACK)   (FMOD_OUTPUT_STATE *output_state, void *object3d, const FMOD_OUTPUT_OBJECT3DINFO *info);

typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_OPENPORT_CALLBACK)         (FMOD_OUTPUT_STATE *output, FMOD_PORT_TYPE portType, FMOD_PORT_INDEX portIndex, int *portId, int *portRate, int *portChannels, FMOD_SOUND_FORMAT *portFormat);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_CLOSEPORT_CALLBACK)        (FMOD_OUTPUT_STATE *output, int portId);
    

/*
    FMOD_OUTPUT_STATE functions
*/
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_READFROMMIXER)             (FMOD_OUTPUT_STATE *output_state, void *buffer, unsigned int length);
typedef FMOD_RESULT (F_CALLBACK *FMOD_OUTPUT_COPYPORT)                  (FMOD_OUTPUT_STATE *output, int portId, void *buffer, unsigned int length);
typedef void *      (F_CALLBACK *FMOD_OUTPUT_ALLOC)                     (unsigned int size, unsigned int align, const char *file, int line);
typedef void        (F_CALLBACK *FMOD_OUTPUT_FREE)                      (void *ptr, const char *file, int line);
typedef void        (F_CALLBACK *FMOD_OUTPUT_LOG)                       (FMOD_DEBUG_FLAGS level, const char *file, int line, const char *function, const char *string, ...);


/*
[STRUCTURE]
[
    [DESCRIPTION]
    When creating an output, declare one of these and provide the relevant callbacks and name for FMOD to use when it creates and uses an output of this type.

    [REMARKS]
    There are several methods for driving the FMOD mixer to service the audio hardware.

    * Polled: if the audio hardware must be polled regularly set 'polling' to TRUE, FMOD will create a mixer thread that calls back via FMOD_OUTPUT_GETPOSITION_CALLBACK. Once an entire block of samples have played FMOD will call FMOD_OUTPUT_LOCK_CALLBACK to allow you to provide a destination pointer to write the next mix.
    * Callback: if the audio hardware provides a callback where you must provide a buffer of samples then set 'polling' to FALSE and directly call FMOD_OUTPUT_READFROMMIXER.
    * Synchronization: if the audio hardware provides a synchronization primitive to wait on then set 'polling' to FALSE and give a FMOD_OUTPUT_MIXER_CALLBACK pointer. FMOD will create a mixer thread and call you repeatedly once FMOD_OUTPUT_START_CALLBACK has finished, you must wait on your primitive in this callback and upon wake call FMOD_OUTPUT_READFROMMIXER.
    * Non-realtime: if you are writing a file or driving a non-realtime output call FMOD_OUTPUT_READFROMMIXER from FMOD_OUTPUT_UPDATE_CALLBACK.

    Callbacks marked with 'user thread' will be called in response to the user of the FMOD low level API, in the case of the Studio runtime API, the user is the Studio Update thread.

    Members marked with [r] mean read only for the developer, read/write for the FMOD system.

    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_OUTPUT_STATE
    FMOD_OUTPUT_GETNUMDRIVERS_CALLBACK
    FMOD_OUTPUT_GETDRIVERINFO_CALLBACK
    FMOD_OUTPUT_INIT_CALLBACK
    FMOD_OUTPUT_START_CALLBACK
    FMOD_OUTPUT_STOP_CALLBACK
    FMOD_OUTPUT_CLOSE_CALLBACK
    FMOD_OUTPUT_UPDATE_CALLBACK
    FMOD_OUTPUT_GETHANDLE_CALLBACK
    FMOD_OUTPUT_GETPOSITION_CALLBACK
    FMOD_OUTPUT_LOCK_CALLBACK
    FMOD_OUTPUT_UNLOCK_CALLBACK
    FMOD_OUTPUT_MIXER_CALLBACK
    FMOD_OUTPUT_OBJECT3DGETINFO_CALLBACK
    FMOD_OUTPUT_OBJECT3DALLOC_CALLBACK
    FMOD_OUTPUT_OBJECT3DFREE_CALLBACK
    FMOD_OUTPUT_OBJECT3DUPDATE_CALLBACK
]
*/
typedef struct FMOD_OUTPUT_DESCRIPTION
{
    unsigned int                            apiversion;         /* [w] The output plugin API version this plugin is built for. Set to this to FMOD_OUTPUT_PLUGIN_VERSION. */
    const char                             *name;               /* [w] Name of the output plugin. */
    unsigned int                            version;            /* [w] Version of the output plugin. */
    int                                     polling;            /* [w] If TRUE (non-zero) a mixer thread is created that calls FMOD_OUTPUT_GETPOSITION_CALLBACK / FMOD_OUTPUT_LOCK_CALLBACK / FMOD_OUTPUT_UNLOCK_CALLBACK to drive the mixer. If FALSE (zero) you must call FMOD_OUTPUT_READFROMMIXER to drive the mixer yourself. */
    FMOD_OUTPUT_GETNUMDRIVERS_CALLBACK      getnumdrivers;      /* [w] Required user thread callback to provide the number of attached sound devices. Called from System::getNumDrivers. */
    FMOD_OUTPUT_GETDRIVERINFO_CALLBACK      getdriverinfo;      /* [w] Required user thread callback to provide information about a particular sound device. Called from System::getDriverInfo. */
    FMOD_OUTPUT_INIT_CALLBACK               init;               /* [w] Required user thread callback to allocate resources and provide information about hardware capabilities. Called from System::init. */
    FMOD_OUTPUT_START_CALLBACK              start;              /* [w] Optional user thread callback just before mixing should begin, calls to FMOD_OUTPUT_GETPOSITION_CALLBACK / FMOD_OUTPUT_LOCK_CALLBACK / FMOD_OUTPUT_UNLOCK_CALLBACK / FMOD_OUTPUT_MIXER_CALLBACK will start, you may call FMOD_OUTPUT_READFROMMIXER after this point. Called from System::init. */
    FMOD_OUTPUT_STOP_CALLBACK               stop;               /* [w] Optional user thread callback just after mixing has finished, calls to FMOD_OUTPUT_GETPOSITION_CALLBACK / FMOD_OUTPUT_LOCK_CALLBACK / FMOD_OUTPUT_UNLOCK_CALLBACK / FMOD_OUTPUT_MIXER_CALLBACK have stopped, you may not call FMOD_OUTPUT_READFROMMIXER after this point. Called from System::close. */
    FMOD_OUTPUT_CLOSE_CALLBACK              close;              /* [w] Required user thread callback to clean up resources allocated during FMOD_OUTPUT_INIT_CALLBACK. Called from System::init and System::close. */
    FMOD_OUTPUT_UPDATE_CALLBACK             update;             /* [w] Optional user thread callback once per frame to update internal state. Called from System::update. */
    FMOD_OUTPUT_GETHANDLE_CALLBACK          gethandle;          /* [w] Optional user thread callback to provide a pointer to the internal device object used to share with other audio systems. Called from System::getOutputHandle. */
    FMOD_OUTPUT_GETPOSITION_CALLBACK        getposition;        /* [w] Required mixer thread callback (if 'polling' is TRUE) to provide the hardware playback position in the output ring buffer. Called before a mix. */
    FMOD_OUTPUT_LOCK_CALLBACK               lock;               /* [w] Required mixer thread callback (if 'polling' is TRUE) to provide a pointer the mixer can write to for the next block of audio data. Called before a mix. */
    FMOD_OUTPUT_UNLOCK_CALLBACK             unlock;             /* [w] Optional mixer thread callback (if 'polling' is TRUE) to signify the mixer has finished writing to the pointer from FMOD_OUTPUT_LOCK_CALLBACK. Called after a mix. */
    FMOD_OUTPUT_MIXER_CALLBACK              mixer;              /* [w] Optional mixer thread callback (if 'polling' is FALSE) called repeatedly to give a thread for waiting on an audio hardware synchronization primitive (see remarks for details). Ensure you have a reasonable timeout (~200ms) on your synchronization primitive and allow this callback to return once per wakeup to avoid deadlocks. */
    FMOD_OUTPUT_OBJECT3DGETINFO_CALLBACK    object3dgetinfo;    /* [w] Optional mixer thread callback to provide information about the capabilities of 3D object hardware. Called during a mix. */
    FMOD_OUTPUT_OBJECT3DALLOC_CALLBACK      object3dalloc;      /* [w] Optional mixer thread callback to reserve a hardware resources for a single 3D object. Called during a mix. */
    FMOD_OUTPUT_OBJECT3DFREE_CALLBACK       object3dfree;       /* [w] Optional mixer thread callback to release a hardware resource previously acquired with FMOD_OUTPUT_OBJECT3DALLOC_CALLBACK. Called during a mix. */
    FMOD_OUTPUT_OBJECT3DUPDATE_CALLBACK     object3dupdate;     /* [w] Optional mixer thread callback once for every acquired 3D object every mix to provide 3D information and buffered audio. Called during a mix. */
    FMOD_OUTPUT_OPENPORT_CALLBACK           openport;           /* [w] Optional main thread callback to open an auxiliary output port on the device. */
    FMOD_OUTPUT_CLOSEPORT_CALLBACK          closeport;          /* [w] Optional main thread callback to close an auxiliary output port on the device. */
} FMOD_OUTPUT_DESCRIPTION;


/*
[STRUCTURE]
[
    [DESCRIPTION]
    Output object state passed into every callback provides access to plugin developers data and system functionality.

    [REMARKS]
    Members marked with [r] mean read only for the developer, read/write for the FMOD system.
    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_OUTPUT_DESCRIPTION
]
*/
struct FMOD_OUTPUT_STATE
{
    void                       *plugindata;     /* [w] Pointer used to store any plugin specific state so it's available in all callbacks. */
    FMOD_OUTPUT_READFROMMIXER   readfrommixer;  /* [r] Function to execute the mixer producing a buffer of audio. Used to control when the mix occurs manually as an alternative to FMOD_OUTPUT_DESCRIPTION::polling == TRUE. */
    FMOD_OUTPUT_ALLOC           alloc;          /* [r] Function to allocate memory using the FMOD memory system. */
    FMOD_OUTPUT_FREE            free;           /* [r] Function to free memory allocated with FMOD_OUTPUT_ALLOC. */
    FMOD_OUTPUT_LOG             log;            /* [r] Function to write to the FMOD logging system. */
    FMOD_OUTPUT_COPYPORT        copyport;       /* [r] Function to copy the output from the mixer for the given auxiliary port */
};


/*
[STRUCTURE]
[
    [DESCRIPTION]
    This structure is passed to the plugin via FMOD_OUTPUT_OBJECT3DUPDATE_CALLBACK, so that whatever object based panning solution available can position it in the speakers correctly.
    Object based panning is a 3D panning solution that sends a mono only signal to a hardware device, such as Dolby Atmos or other similar panning solutions.

    [REMARKS]
    FMOD does not attenuate the buffer, but provides a 'gain' parameter that the user must use to scale the buffer by.  Rather than pre-attenuating the buffer, the plugin developer
    can access untouched data for other purposes, like reverb sending for example.
    The 'gain' parameter is based on the user's 3D custom rolloff model.  
    
    Members marked with [r] mean read only for the developer, read/write for the FMOD system.
    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_OUTPUT_OBJECT3DUPDATE_CALLBACK
]
*/
struct FMOD_OUTPUT_OBJECT3DINFO
{
    float          *buffer;         /* [r] Mono PCM floating point buffer. This buffer needs to be scaled by the gain value to get distance attenuation.  */
    unsigned int    bufferlength;   /* [r] Length in PCM samples of buffer. */
    FMOD_VECTOR     position;       /* [r] Vector relative between object and listener. */
    float           gain;           /* [r] 0.0 to 1.0 - 1 = 'buffer' is not attenuated, 0 = 'buffer' is fully attenuated. */
    float           spread;         /* [r] 0 - 360 degrees.  0 = point source, 360 = sound is spread around all speakers */
    float           priority;       /* [r] 0.0 to 1.0 - 0 = most important, 1 = least important. Based on height and distance (height is more important). */
};

#endif /* _FMOD_OUTPUT_H */
