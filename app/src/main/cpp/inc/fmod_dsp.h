/* ========================================================================================== */
/* FMOD Studio - DSP header file. Copyright (c), Firelight Technologies Pty, Ltd. 2004-2017.  */
/*                                                                                            */
/* Use this header if you are interested in delving deeper into the FMOD software mixing /    */
/* DSP engine.                                                                                */
/* Also use this header if you are wanting to develop your own DSP plugin to use with FMOD's  */
/* dsp system.  With this header you can make your own DSP plugin that FMOD can               */
/* register and use.  See the documentation and examples on how to make a working plugin.     */
/*                                                                                            */
/* ========================================================================================== */

#ifndef _FMOD_DSP_H
#define _FMOD_DSP_H

#include "fmod_dsp_effects.h"

typedef struct FMOD_DSP_STATE FMOD_DSP_STATE;

/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for FMOD_DSP_PROCESS_CALLBACK input and output buffers.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_DESCRIPTION
]
*/
typedef struct FMOD_DSP_BUFFER_ARRAY
{
    int                numbuffers;              /* [r/w] number of buffers */
    int               *buffernumchannels;       /* [r/w] array of number of channels for each buffer */
    FMOD_CHANNELMASK  *bufferchannelmask;       /* [r/w] array of channel masks for each buffer */
    float            **buffers;                 /* [r/w] array of buffers */
    FMOD_SPEAKERMODE   speakermode;             /* [r/w] speaker mode for all buffers in the array */
} FMOD_DSP_BUFFER_ARRAY;


/*
[ENUM]
[
    [DESCRIPTION]
    Operation type for FMOD_DSP_PROCESS_CALLBACK.

    [REMARKS]
    A process callback will be called twice per mix for a DSP unit.  Once with the FMOD_DSP_PROCESS_QUERY command, then conditionally, FMOD_DSP_PROCESS_PERFORM.<br>
    FMOD_DSP_PROCESS_QUERY is to be handled only by filling out the outputarray information, and returning a relevant return code.<br>
    It should not really do any logic besides checking and returning one of the following codes:<br>
    - FMOD_OK - Meaning yes, it should execute the dsp process function with FMOD_DSP_PROCESS_PERFORM<br>
    - FMOD_ERR_DSP_DONTPROCESS - Meaning no, it should skip the process function and not call it with FMOD_DSP_PROCESS_PERFORM.<br>
    - FMOD_ERR_DSP_SILENCE - Meaning no, it should skip the process function and not call it with FMOD_DSP_PROCESS_PERFORM, AND, tell the signal chain to follow that it is now idle, so that no more processing happens down the chain.<br>
    If audio is to be processed, 'outbufferarray' must be filled with the expected output format, channel count and mask.  Mask can be 0.<br>
    <br>
    FMOD_DSP_PROCESS_PROCESS  is to be handled by reading the data from the input, processing it, and writing it to the output.  Always write to the output buffer and fill it fully to avoid unpredictable audio output.<br>
    Always return FMOD_OK, the return value is ignored from the process stage.

    [SEE_ALSO]
    FMOD_DSP_DESCRIPTION
]
*/
typedef enum
{
    FMOD_DSP_PROCESS_PERFORM,                   /* Process the incoming audio in 'inbufferarray' and output to 'outbufferarray'. */
    FMOD_DSP_PROCESS_QUERY                      /* The DSP is being queried for the expected output format and whether it needs to process audio or should be bypassed.  The function should return FMOD_OK, or FMOD_ERR_DSP_DONTPROCESS or FMOD_ERR_DSP_SILENCE if audio can pass through unprocessed. See remarks for more.  If audio is to be processed, 'outbufferarray' must be filled with the expected output format, channel count and mask. */
} FMOD_DSP_PROCESS_OPERATION;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Complex number structure used for holding FFT frequency domain-data for FMOD_FFTREAL and FMOD_IFFTREAL DSP functions.

    [REMARKS]

    [SEE_ALSO]    
    FMOD_DSP_STATE_FUNCTIONS
    FMOD_DSP_STATE_DFT_FUNCTIONS
]
*/
typedef struct FMOD_COMPLEX
{
    float real; /* Real component */
    float imag; /* Imaginary component */
} FMOD_COMPLEX;


/*
[ENUM]
[
    [DESCRIPTION]
    Flags for the FMOD_DSP_PAN_SUMSURROUNDMATRIX_FUNC function.

    [REMARKS]
    This functionality is experimental, please contact support@fmod.org for more information.

    [SEE_ALSO]
    FMOD_DSP_STATE_PAN_FUNCTIONS
]
*/
typedef enum FMOD_DSP_PAN_SURROUND_FLAGS
{
    FMOD_DSP_PAN_SURROUND_DEFAULT = 0,
    FMOD_DSP_PAN_SURROUND_ROTATION_NOT_BIASED = 1,

    FMOD_DSP_PAN_SURROUND_FLAGS_FORCEINT = 65536     /* Makes sure this enum is signed 32bit. */
} FMOD_DSP_PAN_SURROUND_FLAGS;


/*
    FMOD_DSP_DESCRIPTION callbacks
*/
typedef FMOD_RESULT (F_CALL *FMOD_DSP_CREATE_CALLBACK)                    (FMOD_DSP_STATE *dsp_state);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_RELEASE_CALLBACK)                   (FMOD_DSP_STATE *dsp_state);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_RESET_CALLBACK)                     (FMOD_DSP_STATE *dsp_state);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_READ_CALLBACK)                      (FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int *outchannels);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PROCESS_CALLBACK)                   (FMOD_DSP_STATE *dsp_state, unsigned int length, const FMOD_DSP_BUFFER_ARRAY *inbufferarray, FMOD_DSP_BUFFER_ARRAY *outbufferarray, FMOD_BOOL inputsidle, FMOD_DSP_PROCESS_OPERATION op);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SETPOSITION_CALLBACK)               (FMOD_DSP_STATE *dsp_state, unsigned int pos);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SHOULDIPROCESS_CALLBACK)            (FMOD_DSP_STATE *dsp_state, FMOD_BOOL inputsidle, unsigned int length, FMOD_CHANNELMASK inmask, int inchannels, FMOD_SPEAKERMODE speakermode);

typedef FMOD_RESULT (F_CALL *FMOD_DSP_SETPARAM_FLOAT_CALLBACK)            (FMOD_DSP_STATE *dsp_state, int index, float value);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SETPARAM_INT_CALLBACK)              (FMOD_DSP_STATE *dsp_state, int index, int value);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SETPARAM_BOOL_CALLBACK)             (FMOD_DSP_STATE *dsp_state, int index, FMOD_BOOL value);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SETPARAM_DATA_CALLBACK)             (FMOD_DSP_STATE *dsp_state, int index, void *data, unsigned int length);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETPARAM_FLOAT_CALLBACK)            (FMOD_DSP_STATE *dsp_state, int index, float *value, char *valuestr);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETPARAM_INT_CALLBACK)              (FMOD_DSP_STATE *dsp_state, int index, int *value, char *valuestr);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETPARAM_BOOL_CALLBACK)             (FMOD_DSP_STATE *dsp_state, int index, FMOD_BOOL *value, char *valuestr);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETPARAM_DATA_CALLBACK)             (FMOD_DSP_STATE *dsp_state, int index, void **data, unsigned int *length, char *valuestr);

typedef FMOD_RESULT (F_CALL *FMOD_DSP_SYSTEM_REGISTER_CALLBACK)           (FMOD_DSP_STATE *dsp_state);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SYSTEM_DEREGISTER_CALLBACK)         (FMOD_DSP_STATE *dsp_state);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_SYSTEM_MIX_CALLBACK)                (FMOD_DSP_STATE *dsp_state, int stage);


/*
    FMOD_DSP_STATE functions
*/
typedef void *      (F_CALL *FMOD_DSP_ALLOC_FUNC)                         (unsigned int size, FMOD_MEMORY_TYPE type, const char *sourcestr);
typedef void *      (F_CALL *FMOD_DSP_REALLOC_FUNC)                       (void *ptr, unsigned int size, FMOD_MEMORY_TYPE type, const char *sourcestr);
typedef void        (F_CALL *FMOD_DSP_FREE_FUNC)                          (void *ptr, FMOD_MEMORY_TYPE type, const char *sourcestr);
typedef void        (F_CALL *FMOD_DSP_LOG_FUNC)                           (FMOD_DEBUG_FLAGS level, const char *file, int line, const char *function, const char *string, ...);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETSAMPLERATE_FUNC)                 (FMOD_DSP_STATE *dsp_state, int *rate);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETBLOCKSIZE_FUNC)                  (FMOD_DSP_STATE *dsp_state, unsigned int *blocksize);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETSPEAKERMODE_FUNC)                (FMOD_DSP_STATE *dsp_state, FMOD_SPEAKERMODE *speakermode_mixer, FMOD_SPEAKERMODE *speakermode_output);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETCLOCK_FUNC)                      (FMOD_DSP_STATE *dsp_state, unsigned long long *clock, unsigned int *offset, unsigned int *length);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETLISTENERATTRIBUTES_FUNC)         (FMOD_DSP_STATE *dsp_state, int *numlisteners, FMOD_3D_ATTRIBUTES *attributes);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_GETUSERDATA_FUNC)                   (FMOD_DSP_STATE *dsp_state, void **userdata);

typedef FMOD_RESULT (F_CALL *FMOD_DSP_DFT_FFTREAL_FUNC)                   (FMOD_DSP_STATE *dsp_state, int size, const float *signal, FMOD_COMPLEX* dft, const float *window, int signalhop);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_DFT_IFFTREAL_FUNC)                  (FMOD_DSP_STATE *dsp_state, int size, const FMOD_COMPLEX *dft, float* signal, const float *window, int signalhop);

typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_SUMMONOMATRIX_FUNC)             (FMOD_DSP_STATE *dsp_state, int sourceSpeakerMode, float lowFrequencyGain, float overallGain, float *matrix);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_SUMSTEREOMATRIX_FUNC)           (FMOD_DSP_STATE *dsp_state, int sourceSpeakerMode, float pan, float lowFrequencyGain, float overallGain, int matrixHop, float *matrix);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_SUMSURROUNDMATRIX_FUNC)         (FMOD_DSP_STATE *dsp_state, int sourceSpeakerMode, int targetSpeakerMode, float direction, float extent, float rotation, float lowFrequencyGain, float overallGain, int matrixHop, float *matrix, FMOD_DSP_PAN_SURROUND_FLAGS flags);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_SUMMONOTOSURROUNDMATRIX_FUNC)   (FMOD_DSP_STATE *dsp_state, int targetSpeakerMode, float direction, float extent, float lowFrequencyGain, float overallGain, int matrixHop, float *matrix);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_SUMSTEREOTOSURROUNDMATRIX_FUNC) (FMOD_DSP_STATE *dsp_state, int targetSpeakerMode, float direction, float extent, float rotation, float lowFrequencyGain, float overallGain, int matrixHop, float *matrix);
typedef FMOD_RESULT (F_CALL *FMOD_DSP_PAN_GETROLLOFFGAIN_FUNC)            (FMOD_DSP_STATE *dsp_state, FMOD_DSP_PAN_3D_ROLLOFF_TYPE rolloff, float distance, float mindistance, float maxdistance, float *gain);


/*
[DEFINE]
[
    [NAME]
    FMOD_DSP_GETPARAM_VALUESTR_LENGTH
    
    [DESCRIPTION]
    Length in bytes of the buffer pointed to by the valuestr argument of FMOD_DSP_GETPARAM_XXXX_CALLBACK functions.
    
    [REMARKS]
    DSP plugins should not copy more than this number of bytes into the buffer or memory corruption will occur.
    
    [SEE_ALSO]
    FMOD_DSP_GETPARAM_FLOAT_CALLBACK
    FMOD_DSP_GETPARAM_INT_CALLBACK
    FMOD_DSP_GETPARAM_BOOL_CALLBACK
    FMOD_DSP_GETPARAM_DATA_CALLBACK
]
*/
#define FMOD_DSP_GETPARAM_VALUESTR_LENGTH 32
/* [DEFINE_END] */

/*
[ENUM]
[
    [DESCRIPTION]   
    DSP parameter types.

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef enum
{
    FMOD_DSP_PARAMETER_TYPE_FLOAT,              /* FMOD_DSP_PARAMETER_DESC will use the FMOD_DSP_PARAMETER_DESC_FLOAT. */
    FMOD_DSP_PARAMETER_TYPE_INT,                /* FMOD_DSP_PARAMETER_DESC will use the FMOD_DSP_PARAMETER_DESC_INT. */
    FMOD_DSP_PARAMETER_TYPE_BOOL,               /* FMOD_DSP_PARAMETER_DESC will use the FMOD_DSP_PARAMETER_DESC_BOOL. */
    FMOD_DSP_PARAMETER_TYPE_DATA,               /* FMOD_DSP_PARAMETER_DESC will use the FMOD_DSP_PARAMETER_DESC_DATA. */

    FMOD_DSP_PARAMETER_TYPE_MAX,                /* Maximum number of DSP parameter types. */
    FMOD_DSP_PARAMETER_TYPE_FORCEINT = 65536    /* Makes sure this enum is signed 32bit. */
} FMOD_DSP_PARAMETER_TYPE;


/*
[ENUM]
[
    [DESCRIPTION]   
    DSP float parameter mappings. These determine how values are mapped across dials and automation curves.

    [REMARKS]
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_AUTO generates a mapping based on range and units. For example, if the units are in Hertz and the range is with-in the audio spectrum, a Bark scale will be chosen. Logarithmic scales may also be generated for ranges above zero spanning several orders of magnitude.

    [SEE_ALSO]
    FMOD_DSP_PARAMETER_FLOAT_MAPPING
]
*/
typedef enum
{
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_LINEAR,               /* Values mapped linearly across range. */
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_AUTO,                 /* A mapping is automatically chosen based on range and units.  See remarks. */
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_PIECEWISE_LINEAR,     /* Values mapped in a piecewise linear fashion defined by FMOD_DSP_PARAMETER_FLOAT_MAPPING_PIECEWISE_LINEAR. */

    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_FORCEINT = 65536      /* Makes sure this enum is signed 32bit. */
} FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE;

/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a piecewise linear mapping.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE
    FMOD_DSP_PARAMETER_FLOAT_MAPPING
]
*/
typedef struct FMOD_DSP_PARAMETER_FLOAT_MAPPING_PIECEWISE_LINEAR
{
    int numpoints;                              /* [w] The number of <position, value> pairs in the piecewise mapping (at least 2). */
    float *pointparamvalues;                    /* [w] The values in the parameter's units for each point */
    float *pointpositions;                      /* [w] The positions along the control's scale (e.g. dial angle) corresponding to each parameter value.  The range of this scale is arbitrary and all positions will be relative to the minimum and maximum values (e.g. [0,1,3] is equivalent to [1,2,4] and [2,4,8]).  If this array is zero, pointparamvalues will be distributed with equal spacing. */
} FMOD_DSP_PARAMETER_FLOAT_MAPPING_PIECEWISE_LINEAR;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a mapping for a DSP unit's float parameter.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_PIECEWISE_LINEAR
    FMOD_DSP_PARAMETER_DESC_FLOAT
]
*/
typedef struct FMOD_DSP_PARAMETER_FLOAT_MAPPING
{
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE type;
    FMOD_DSP_PARAMETER_FLOAT_MAPPING_PIECEWISE_LINEAR piecewiselinearmapping; /* [w] Only required for FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_PIECEWISE_LINEAR type mapping. */
} FMOD_DSP_PARAMETER_FLOAT_MAPPING;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a float parameter for a DSP unit.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterFloat
    DSP::getParameterFloat
    FMOD_DSP_PARAMETER_DESC
    FMOD_DSP_PARAMETER_FLOAT_MAPPING
]
*/
typedef struct FMOD_DSP_PARAMETER_DESC_FLOAT
{
    float                     min;                      /* [w] Minimum parameter value. */
    float                     max;                      /* [w] Maximum parameter value. */
    float                     defaultval;               /* [w] Default parameter value. */
    FMOD_DSP_PARAMETER_FLOAT_MAPPING mapping;           /* [w] How the values are distributed across dials and automation curves (e.g. linearly, exponentially etc). */
} FMOD_DSP_PARAMETER_DESC_FLOAT;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a int parameter for a DSP unit.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterInt
    DSP::getParameterInt
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_DESC_INT
{
    int                       min;                      /* [w] Minimum parameter value. */
    int                       max;                      /* [w] Maximum parameter value. */
    int                       defaultval;               /* [w] Default parameter value. */
    FMOD_BOOL                 goestoinf;                /* [w] Whether the last value represents infiniy. */
    const char* const*        valuenames;               /* [w] Names for each value.  There should be as many strings as there are possible values (max - min + 1).  Optional. */
} FMOD_DSP_PARAMETER_DESC_INT;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a boolean parameter for a DSP unit.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterBool
    DSP::getParameterBool
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_DESC_BOOL
{
    FMOD_BOOL                 defaultval;               /* [w] Default parameter value. */
    const char* const*        valuenames;               /* [w] Names for false and true, respectively.  There should be two strings.  Optional. */
} FMOD_DSP_PARAMETER_DESC_BOOL;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure to define a data parameter for a DSP unit.  Use 0 or above for custom types.  This parameter will be treated specially by the system if set to one of the FMOD_DSP_PARAMETER_DATA_TYPE values.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_DESC_DATA
{
    int                       datatype;                 /* [w] The type of data for this parameter.  Use 0 or above for custom types or set to one of the FMOD_DSP_PARAMETER_DATA_TYPE values. */
} FMOD_DSP_PARAMETER_DESC_DATA;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Base Structure for DSP parameter descriptions.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterFloat
    DSP::getParameterFloat
    DSP::setParameterInt
    DSP::getParameterInt
    DSP::setParameterBool
    DSP::getParameterBool
    DSP::setParameterData
    DSP::getParameterData
    FMOD_DSP_PARAMETER_DESC_FLOAT
    FMOD_DSP_PARAMETER_DESC_INT
    FMOD_DSP_PARAMETER_DESC_BOOL
    FMOD_DSP_PARAMETER_DESC_DATA
]
*/
typedef struct FMOD_DSP_PARAMETER_DESC
{
    FMOD_DSP_PARAMETER_TYPE   type;                 /* [w] Type of this parameter. */
    char                      name[16];             /* [w] Name of the parameter to be displayed (ie "Cutoff frequency"). */
    char                      label[16];            /* [w] Short string to be put next to value to denote the unit type (ie "hz"). */
    const char               *description;          /* [w] Description of the parameter to be displayed as a help item / tooltip for this parameter. */

    union
    {
        FMOD_DSP_PARAMETER_DESC_FLOAT   floatdesc;  /* [w] Struct containing information about the parameter in floating point format.  Use when type is FMOD_DSP_PARAMETER_TYPE_FLOAT. */
        FMOD_DSP_PARAMETER_DESC_INT     intdesc;    /* [w] Struct containing information about the parameter in integer format.  Use when type is FMOD_DSP_PARAMETER_TYPE_INT. */
        FMOD_DSP_PARAMETER_DESC_BOOL    booldesc;   /* [w] Struct containing information about the parameter in boolean format.  Use when type is FMOD_DSP_PARAMETER_TYPE_BOOL. */
        FMOD_DSP_PARAMETER_DESC_DATA    datadesc;   /* [w] Struct containing information about the parameter in data format.  Use when type is FMOD_DSP_PARAMETER_TYPE_DATA. */
    };
} FMOD_DSP_PARAMETER_DESC;


/*
[ENUM]
[
    [DESCRIPTION]   
    Built-in types for the 'datatype' member of FMOD_DSP_PARAMETER_DESC_DATA.  Data parameters of type other than FMOD_DSP_PARAMETER_DATA_TYPE_USER will be treated specially by the system. 

    [REMARKS]

    [SEE_ALSO]
    FMOD_DSP_PARAMETER_DESC_DATA
    FMOD_DSP_PARAMETER_OVERALLGAIN
    FMOD_DSP_PARAMETER_3DATTRIBUTES
    FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI
    FMOD_DSP_PARAMETER_SIDECHAIN
]
*/
typedef enum
{
    FMOD_DSP_PARAMETER_DATA_TYPE_USER = 0,                  /* The default data type.  All user data types should be 0 or above. */
    FMOD_DSP_PARAMETER_DATA_TYPE_OVERALLGAIN = -1,          /* The data type for FMOD_DSP_PARAMETER_OVERALLGAIN parameters.  There should a maximum of one per DSP. */
    FMOD_DSP_PARAMETER_DATA_TYPE_3DATTRIBUTES = -2,         /* The data type for FMOD_DSP_PARAMETER_3DATTRIBUTES parameters.  There should a maximum of one per DSP. */
    FMOD_DSP_PARAMETER_DATA_TYPE_SIDECHAIN = -3,            /* The data type for FMOD_DSP_PARAMETER_SIDECHAIN parameters.  There should a maximum of one per DSP. */
    FMOD_DSP_PARAMETER_DATA_TYPE_FFT = -4,                  /* The data type for FMOD_DSP_PARAMETER_FFT parameters.  There should a maximum of one per DSP. */
    FMOD_DSP_PARAMETER_DATA_TYPE_3DATTRIBUTES_MULTI = -5,   /* The data type for FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI parameters.  There should a maximum of one per DSP. */
} FMOD_DSP_PARAMETER_DATA_TYPE;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for data parameters of type FMOD_DSP_PARAMETER_DATA_TYPE_OVERALLGAIN.
    A parameter of this type is used in effects that affect the overgain of the signal in a predictable way.
    This parameter is read by the system to determine the effect's gain for voice virtualization.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_OVERALLGAIN
{
    float linear_gain;                                  /* [r] The overall linear gain of the effect on the direct signal path */
    float linear_gain_additive;                         /* [r] Additive gain, for parallel signal paths */
} FMOD_DSP_PARAMETER_OVERALLGAIN;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for data parameters of type FMOD_DSP_PARAMETER_DATA_TYPE_3DATTRIBUTES.
    
    A parameter of this type is used in effects that respond to a 3D position.

    [REMARKS]
    The FMOD::Studio::System will set this parameter automatically if an FMOD::Studio::EventInstance position
    changes, however if using the low level FMOD::System you must set this DSP parameter explicitly.

    FMOD will convert passed in co-ordinates to left-handed for the plugin if the System was initialized with the FMOD_INIT_3D_RIGHTHANDED flag.

    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_3DATTRIBUTES
{
    FMOD_3D_ATTRIBUTES relative;                        /* [w] The position of the sound relative to the listener. */
    FMOD_3D_ATTRIBUTES absolute;                        /* [w] The position of the sound in world coordinates. */
} FMOD_DSP_PARAMETER_3DATTRIBUTES;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for data parameters of type FMOD_DSP_PARAMETER_DATA_TYPE_3DATTRIBUTES_MULTI.

    A parameter of this type is used in effects that respond to a 3D position and support multiple listeners.

    [REMARKS]
    The FMOD::Studio::System will set this parameter automatically if an FMOD::Studio::EventInstance position
    changes, however if using the low level FMOD::System you must set this DSP parameter explicitly.

    FMOD will convert passed in co-ordinates to left-handed for the plugin if the System was initialized with the FMOD_INIT_3D_RIGHTHANDED flag.

    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI
{
    int                numlisteners;                    /* [w] The number of listeners. */
    FMOD_3D_ATTRIBUTES relative[FMOD_MAX_LISTENERS];    /* [w] The position of the sound relative to the listeners. */
    float              weight[FMOD_MAX_LISTENERS];      /* [w] The weighting of the listeners where 0 means listener has no contribution and 1 means full contribution. */
    FMOD_3D_ATTRIBUTES absolute;                        /* [w] The position of the sound in world coordinates. */
} FMOD_DSP_PARAMETER_3DATTRIBUTES_MULTI;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for data parameters of type FMOD_DSP_PARAMETER_DATA_TYPE_SIDECHAIN.
    A parameter of this type is declared for effects which support sidechaining.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
]
*/
typedef struct FMOD_DSP_PARAMETER_SIDECHAIN
{
    FMOD_BOOL sidechainenable;                               /* [r/w] Whether sidechains are enabled. */
} FMOD_DSP_PARAMETER_SIDECHAIN;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Structure for data parameters of type FMOD_DSP_PARAMETER_DATA_TYPE_FFT.
    A parameter of this type is declared for the FMOD_DSP_TYPE_FFT effect.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>
    <br>
    Notes on the spectrum data member.  Values inside the float buffer are typically between 0 and 1.0.<br>
    Each top level array represents one PCM channel of data.<br>
    Address data as spectrum[channel][bin].  A bin is 1 fft window entry.<br>
    Only read/display half of the buffer typically for analysis as the 2nd half is usually the same data reversed due to the nature of the way FFT works.<br>

    [SEE_ALSO]    
    FMOD_DSP_PARAMETER_DATA_TYPE
    FMOD_DSP_PARAMETER_DESC
    FMOD_DSP_PARAMETER_DATA_TYPE_FFT
    FMOD_DSP_TYPE
    FMOD_DSP_FFT
]
*/
typedef struct FMOD_DSP_PARAMETER_FFT
{
    int     length;                                    /* [r] Number of entries in this spectrum window.   Divide this by the output rate to get the hz per entry. */
    int     numchannels;                               /* [r] Number of channels in spectrum. */
    float  *spectrum[32];                              /* [r] Per channel spectrum arrays.  See remarks for more. */
} FMOD_DSP_PARAMETER_FFT;


/*
    Helpers for declaring parameters in custom DSPSs
*/
#define FMOD_DSP_INIT_PARAMDESC_FLOAT(_paramstruct, _name, _label, _description, _min, _max, _defaultval) \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_FLOAT; \
    strncpy((_paramstruct).name,  _name,  15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).floatdesc.min          = _min; \
    (_paramstruct).floatdesc.max          = _max; \
    (_paramstruct).floatdesc.defaultval   = _defaultval; \
    (_paramstruct).floatdesc.mapping.type = FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_AUTO;

#define FMOD_DSP_INIT_PARAMDESC_FLOAT_WITH_MAPPING(_paramstruct, _name, _label, _description, _defaultval, _values, _positions); \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_FLOAT; \
    strncpy((_paramstruct).name,  _name , 15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).floatdesc.min          = _values[0]; \
    (_paramstruct).floatdesc.max          = _values[sizeof(_values) / sizeof(float) - 1]; \
    (_paramstruct).floatdesc.defaultval   = _defaultval; \
    (_paramstruct).floatdesc.mapping.type = FMOD_DSP_PARAMETER_FLOAT_MAPPING_TYPE_PIECEWISE_LINEAR; \
    (_paramstruct).floatdesc.mapping.piecewiselinearmapping.numpoints = sizeof(_values) / sizeof(float); \
    (_paramstruct).floatdesc.mapping.piecewiselinearmapping.pointparamvalues = _values; \
    (_paramstruct).floatdesc.mapping.piecewiselinearmapping.pointpositions = _positions;

#define FMOD_DSP_INIT_PARAMDESC_INT(_paramstruct, _name, _label, _description, _min, _max, _defaultval, _goestoinf, _valuenames) \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_INT; \
    strncpy((_paramstruct).name,  _name , 15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).intdesc.min          = _min; \
    (_paramstruct).intdesc.max          = _max; \
    (_paramstruct).intdesc.defaultval   = _defaultval; \
    (_paramstruct).intdesc.goestoinf    = _goestoinf; \
    (_paramstruct).intdesc.valuenames   = _valuenames;

#define FMOD_DSP_INIT_PARAMDESC_INT_ENUMERATED(_paramstruct, _name, _label, _description, _defaultval, _valuenames) \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_INT; \
    strncpy((_paramstruct).name,  _name , 15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).intdesc.min          = 0; \
    (_paramstruct).intdesc.max          = sizeof(_valuenames) / sizeof(char*) - 1; \
    (_paramstruct).intdesc.defaultval   = _defaultval; \
    (_paramstruct).intdesc.goestoinf    = false; \
    (_paramstruct).intdesc.valuenames   = _valuenames;

#define FMOD_DSP_INIT_PARAMDESC_BOOL(_paramstruct, _name, _label, _description, _defaultval, _valuenames) \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_BOOL; \
    strncpy((_paramstruct).name,  _name , 15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).booldesc.defaultval   = _defaultval; \
    (_paramstruct).booldesc.valuenames   = _valuenames;

#define FMOD_DSP_INIT_PARAMDESC_DATA(_paramstruct, _name, _label, _description, _datatype) \
    memset(&(_paramstruct), 0, sizeof(_paramstruct)); \
    (_paramstruct).type         = FMOD_DSP_PARAMETER_TYPE_DATA; \
    strncpy((_paramstruct).name,  _name , 15); \
    strncpy((_paramstruct).label, _label, 15); \
    (_paramstruct).description  = _description; \
    (_paramstruct).datadesc.datatype     = _datatype;

#define FMOD_PLUGIN_SDK_VERSION 110

/*
[STRUCTURE] 
[
    [DESCRIPTION]
    When creating a DSP unit, declare one of these and provide the relevant callbacks and name for FMOD to use when it creates and uses a DSP unit of this type.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>
    <br>
    There are 2 different ways to change a parameter in this architecture.<br>
    One is to use DSP::setParameterFloat / DSP::setParameterInt / DSP::setParameterBool / DSP::setParameterData.  This is platform independant and is dynamic, so new unknown plugins can have their parameters enumerated and used.<br>
    The other is to use DSP::showConfigDialog.  This is platform specific and requires a GUI, and will display a dialog box to configure the plugin.<br>

    [SEE_ALSO]    
    System::createDSP
    DSP::setParameterFloat
    DSP::setParameterInt
    DSP::setParameterBool
    DSP::setParameterData
    FMOD_DSP_STATE
    FMOD_DSP_CREATE_CALLBACK
    FMOD_DSP_RELEASE_CALLBACK
    FMOD_DSP_RESET_CALLBACK
    FMOD_DSP_READ_CALLBACK
    FMOD_DSP_PROCESS_CALLBACK
    FMOD_DSP_SETPOSITION_CALLBACK
    FMOD_DSP_PARAMETER_DESC
    FMOD_DSP_SETPARAM_FLOAT_CALLBACK
    FMOD_DSP_SETPARAM_INT_CALLBACK
    FMOD_DSP_SETPARAM_BOOL_CALLBACK
    FMOD_DSP_SETPARAM_DATA_CALLBACK
    FMOD_DSP_GETPARAM_FLOAT_CALLBACK
    FMOD_DSP_GETPARAM_INT_CALLBACK
    FMOD_DSP_GETPARAM_BOOL_CALLBACK
    FMOD_DSP_GETPARAM_DATA_CALLBACK
    FMOD_DSP_SHOULDIPROCESS_CALLBACK
    FMOD_DSP_SYSTEM_REGISTER_CALLBACK
    FMOD_DSP_SYSTEM_DEREGISTER_CALLBACK
    FMOD_DSP_SYSTEM_MIX_CALLBACK
]
*/
typedef struct FMOD_DSP_DESCRIPTION
{
    unsigned int                        pluginsdkversion;   /* [w] The plugin SDK version this plugin is built for.  Set to this to FMOD_PLUGIN_SDK_VERSION defined above. */
    char                                name[32];           /* [w] The identifier of the DSP. This will also be used as the name of DSP and shouldn't change between versions. */
    unsigned int                        version;            /* [w] Plugin writer's version number. */
    int                                 numinputbuffers;    /* [w] Number of input buffers to process.  Use 0 for DSPs that only generate sound and 1 for effects that process incoming sound. */
    int                                 numoutputbuffers;   /* [w] Number of audio output buffers.  Only one output buffer is currently supported. */
    FMOD_DSP_CREATE_CALLBACK            create;             /* [w] Create callback.  This is called when DSP unit is created.  Can be null. */
    FMOD_DSP_RELEASE_CALLBACK           release;            /* [w] Release callback.  This is called just before the unit is freed so the user can do any cleanup needed for the unit.  Can be null. */
    FMOD_DSP_RESET_CALLBACK             reset;              /* [w] Reset callback.  This is called by the user to reset any history buffers that may need resetting for a filter, when it is to be used or re-used for the first time to its initial clean state.  Use to avoid clicks or artifacts. */
    FMOD_DSP_READ_CALLBACK              read;               /* [w] Read callback.  Processing is done here.  Can be null. */
    FMOD_DSP_PROCESS_CALLBACK           process;            /* [w] Process callback.  Can be specified instead of the read callback if any channel format changes occur between input and output.  This also replaces shouldiprocess and should return an error if the effect is to be bypassed.  Can be null. */
    FMOD_DSP_SETPOSITION_CALLBACK       setposition;        /* [w] Set position callback.  This is called if the unit wants to update its position info but not process data, or reset a cursor position internally if it is reading data from a certain source.  Can be null. */

    int                                 numparameters;      /* [w] Number of parameters used in this filter.  The user finds this with DSP::getNumParameters */
    FMOD_DSP_PARAMETER_DESC           **paramdesc;          /* [w] Variable number of parameter structures. */
    FMOD_DSP_SETPARAM_FLOAT_CALLBACK    setparameterfloat;  /* [w] This is called when the user calls DSP::setParameterFloat. Can be null. */
    FMOD_DSP_SETPARAM_INT_CALLBACK      setparameterint;    /* [w] This is called when the user calls DSP::setParameterInt.   Can be null. */
    FMOD_DSP_SETPARAM_BOOL_CALLBACK     setparameterbool;   /* [w] This is called when the user calls DSP::setParameterBool.  Can be null. */
    FMOD_DSP_SETPARAM_DATA_CALLBACK     setparameterdata;   /* [w] This is called when the user calls DSP::setParameterData.  Can be null. */
    FMOD_DSP_GETPARAM_FLOAT_CALLBACK    getparameterfloat;  /* [w] This is called when the user calls DSP::getParameterFloat. Can be null. */
    FMOD_DSP_GETPARAM_INT_CALLBACK      getparameterint;    /* [w] This is called when the user calls DSP::getParameterInt.   Can be null. */
    FMOD_DSP_GETPARAM_BOOL_CALLBACK     getparameterbool;   /* [w] This is called when the user calls DSP::getParameterBool.  Can be null. */
    FMOD_DSP_GETPARAM_DATA_CALLBACK     getparameterdata;   /* [w] This is called when the user calls DSP::getParameterData.  Can be null. */
    FMOD_DSP_SHOULDIPROCESS_CALLBACK    shouldiprocess;     /* [w] This is called before processing.  You can detect if inputs are idle and return FMOD_OK to process, or any other error code to avoid processing the effect.  Use a count down timer to allow effect tails to process before idling! */
    void                               *userdata;           /* [w] Optional. Specify 0 to ignore. This is user data to be attached to the DSP unit during creation.  Access via FMOD_DSP_STATE_FUNCTIONS::getuserdata. */

    FMOD_DSP_SYSTEM_REGISTER_CALLBACK   sys_register;       /* [w] Register callback.  This is called when DSP unit is loaded/registered.  Useful for 'global'/per system object init for plugin.  Can be null. */
    FMOD_DSP_SYSTEM_DEREGISTER_CALLBACK sys_deregister;     /* [w] Deregister callback.  This is called when DSP unit is unloaded/deregistered.  Useful as 'global'/per system object shutdown for plugin.  Can be null. */
    FMOD_DSP_SYSTEM_MIX_CALLBACK        sys_mix;            /* [w] System mix stage callback.  This is called when the mixer starts to execute or is just finishing executing.  Useful for 'global'/per system object once a mix update calls for a plugin.  Can be null. */

} FMOD_DSP_DESCRIPTION;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Struct containing DFT functions to enable a plugin to perform optimized time-frequency domain conversion.

    [REMARKS]
    Members marked with [r] mean read only for the developer, read/write for the FMOD system.

    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_DSP_STATE_FUNCTIONS
]
*/
typedef struct FMOD_DSP_STATE_DFT_FUNCTIONS
{
    FMOD_DSP_DFT_FFTREAL_FUNC  fftreal;        /* [r] Function for performing an FFT on a real signal. */
    FMOD_DSP_DFT_IFFTREAL_FUNC inversefftreal; /* [r] Function for performing an inverse FFT to get a real signal. */
} FMOD_DSP_STATE_DFT_FUNCTIONS;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Struct containing panning helper functions for spatialization plugins.

    [REMARKS]
    These are experimental, please contact support@fmod.org for more information.

    Members marked with [r] mean read only for the developer, read/write for the FMOD system.

    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_DSP_STATE_FUNCTIONS
    FMOD_DSP_PAN_SURROUND_FLAGS
]
*/
typedef struct FMOD_DSP_STATE_PAN_FUNCTIONS
{
    FMOD_DSP_PAN_SUMMONOMATRIX_FUNC             summonomatrix;             /* [r] TBD. */
    FMOD_DSP_PAN_SUMSTEREOMATRIX_FUNC           sumstereomatrix;           /* [r] TBD. */
    FMOD_DSP_PAN_SUMSURROUNDMATRIX_FUNC         sumsurroundmatrix;         /* [r] TBD. */
    FMOD_DSP_PAN_SUMMONOTOSURROUNDMATRIX_FUNC   summonotosurroundmatrix;   /* [r] TBD. */
    FMOD_DSP_PAN_SUMSTEREOTOSURROUNDMATRIX_FUNC sumstereotosurroundmatrix; /* [r] TBD. */
    FMOD_DSP_PAN_GETROLLOFFGAIN_FUNC            getrolloffgain;            /* [r] TBD. */
} FMOD_DSP_STATE_PAN_FUNCTIONS;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    Struct containing functions to give plugin developers the ability to query system state, access system level functionality and helpers.

    [REMARKS]
    Members marked with [r] mean read only for the developer, read/write for the FMOD system.

    Members marked with [w] mean read/write for the developer, read only for the FMOD system.

    [SEE_ALSO]
    FMOD_DSP_STATE
    FMOD_DSP_STATE_DFT_FUNCTIONS
    FMOD_DSP_STATE_PAN_FUNCTIONS
]
*/
typedef struct FMOD_DSP_STATE_FUNCTIONS
{
    FMOD_DSP_ALLOC_FUNC                 alloc;                  /* [r] Function to allocate memory using the FMOD memory system. */
    FMOD_DSP_REALLOC_FUNC               realloc;                /* [r] Function to reallocate memory using the FMOD memory system. */
    FMOD_DSP_FREE_FUNC                  free;                   /* [r] Function to free memory allocated with FMOD_DSP_ALLOC_FUNC. */
    FMOD_DSP_GETSAMPLERATE_FUNC         getsamplerate;          /* [r] Function to query the system sample rate. */
    FMOD_DSP_GETBLOCKSIZE_FUNC          getblocksize;           /* [r] Function to query the system block size, DSPs will be requested to process blocks of varying length up to this size. */
    FMOD_DSP_STATE_DFT_FUNCTIONS       *dft;                    /* [r] Struct containing DFT functions to enable a plugin to perform optimized time-frequency domain conversion. */
    FMOD_DSP_STATE_PAN_FUNCTIONS       *pan;                    /* [r] Struct containing panning helper functions for spatialization plugins. */
    FMOD_DSP_GETSPEAKERMODE_FUNC        getspeakermode;         /* [r] Function to query the system speaker modes.  One is the mixer's default speaker mode, the other is the output mode the system is downmixing or upmixing to.*/
    FMOD_DSP_GETCLOCK_FUNC              getclock;               /* [r] Function to get the clock of the current DSP, as well as the subset of the input buffer that contains the signal. */
    FMOD_DSP_GETLISTENERATTRIBUTES_FUNC getlistenerattributes;  /* [r] Callback for getting the absolute listener attributes set via the API (returned as left-handed co-ordinates). */
    FMOD_DSP_LOG_FUNC                   log;                    /* [r] Function to write to the FMOD logging system. */
    FMOD_DSP_GETUSERDATA_FUNC           getuserdata;            /* [r] Function to get the user data attached to this DSP. See FMOD_DSP_DESCRIPTION::userdata. */
} FMOD_DSP_STATE_FUNCTIONS;


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    DSP plugin structure that is passed into each callback.

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>
    <br>
    'systemobject' is an integer that relates to the System object that created the DSP or registered the DSP plugin.  If only 1 System object is created then it should be 0.  A second object would be 1 and so on.
    FMOD_DSP_STATE_FUNCTIONS::getsamplerate/getblocksize/getspeakermode could return different results so it could be relevant to plugin developers to monitor which object is being used.

    [SEE_ALSO]
    FMOD_DSP_DESCRIPTION
    FMOD_DSP_STATE_FUNCTIONS
]
*/
struct FMOD_DSP_STATE
{
    FMOD_DSP                 *instance;            /* [r] Handle to the FMOD_DSP object the callback is associated with.  Not to be modified.  C++ users cast to FMOD::DSP to use.  */
    void                     *plugindata;          /* [w] Plugin writer created data the output author wants to attach to this object. */
    FMOD_CHANNELMASK          channelmask;         /* [r] Specifies which speakers the DSP effect is active on */
    FMOD_SPEAKERMODE          source_speakermode;  /* [r] Specifies which speaker mode the signal originated for information purposes, ie in case panning needs to be done differently. */
    float                    *sidechaindata;       /* [r] The mixed result of all incoming sidechains is stored at this pointer address. */
    int                       sidechainchannels;   /* [r] The number of channels of pcm data stored within the sidechain buffer. */
    FMOD_DSP_STATE_FUNCTIONS *functions;           /* [r] Struct containing functions to give plugin developers the ability to query system state, access system level functionality and helpers. */
    int                       systemobject;        /* [r] FMOD::System object index, relating to the System object that created this DSP. */
};


/*
    Macro helpers for accessing FMOD_DSP_STATE_FUNCTIONS
*/
#define FMOD_DSP_ALLOC(_state, _size) \
    (_state)->functions->alloc(_size, FMOD_MEMORY_NORMAL, __FILE__)
#define FMOD_DSP_REALLOC(_state, _ptr, _size) \
    (_state)->functions->realloc(_ptr, _size, FMOD_MEMORY_NORMAL, __FILE__)
#define FMOD_DSP_FREE(_state, _ptr) \
    (_state)->functions->free(_ptr, FMOD_MEMORY_NORMAL, __FILE__)
#define FMOD_DSP_LOG(_state, _level, _location, _format, ...) \
    (_state)->functions->log(_level, __FILE__, __LINE__, _location, _format, __VA_ARGS__)
#define FMOD_DSP_GETSAMPLERATE(_state, _rate) \
    (_state)->functions->getsamplerate(_state, _rate)
#define FMOD_DSP_GETBLOCKSIZE(_state, _blocksize) \
    (_state)->functions->getblocksize(_state, _blocksize)
#define FMOD_DSP_GETSPEAKERMODE(_state, _speakermodemix, _speakermodeout) \
    (_state)->functions->getspeakermode(_state, _speakermodemix, _speakermodeout)
#define FMOD_DSP_GETCLOCK(_state, _clock, _offset, _length) \
    (_state)->functions->getclock(_state, _clock, _offset, _length)
#define FMOD_DSP_GETLISTENERATTRIBUTES(_state, _numlisteners, _attributes) \
    (_state)->functions->getlistenerattributes(_state, _numlisteners, _attributes)
#define FMOD_DSP_GETUSERDATA(_state, _userdata) \
    (_state)->functions->getuserdata(_state, _userdata)
#define FMOD_DSP_DFT_FFTREAL(_state, _size, _signal, _dft, _window, _signalhop) \
    (_state)->functions->dft->fftreal(_state, _size, _signal, _dft, _window, _signalhop)
#define FMOD_DSP_DFT_IFFTREAL(_state, _size, _dft, _signal, _window, _signalhop) \
    (_state)->functions->dft->inversefftreal(_state, _size, _dft, _signal, _window, _signalhop)
#define FMOD_DSP_PAN_SUMMONOMATRIX(_state, _sourcespeakermode, _lowfrequencygain, _overallgain, _matrix) \
    (_state)->functions->pan->summonomatrix(_state, _sourcespeakermode, _lowfrequencygain, _overallgain, _matrix)
#define FMOD_DSP_PAN_SUMSTEREOMATRIX(_state, _sourcespeakermode, _pan, _lowfrequencygain, _overallgain, _matrixhop, _matrix) \
    (_state)->functions->pan->sumstereomatrix(_state, _sourcespeakermode, _pan, _lowfrequencygain, _overallgain, _matrixhop, _matrix)
#define FMOD_DSP_PAN_SUMSURROUNDMATRIX(_state, _sourcespeakermode, _targetspeakermode, _direction, _extent, _rotation, _lowfrequencygain, _overallgain, _matrixhop, _matrix, _flags) \
    (_state)->functions->pan->sumsurroundmatrix(_state, _sourcespeakermode, _targetspeakermode, _direction, _extent, _rotation, _lowfrequencygain, _overallgain, _matrixhop, _matrix, _flags)
#define FMOD_DSP_PAN_SUMMONOTOSURROUNDMATRIX(_state, _targetspeakermode, _direction, _extent, _lowfrequencygain, _overallgain, _matrixhop, _matrix) \
    (_state)->functions->pan->summonotosurroundmatrix(_state, _targetspeakermode, _direction, _extent, _lowfrequencygain, _overallgain, _matrixhop, _matrix)
#define FMOD_DSP_PAN_SUMSTEREOTOSURROUNDMATRIX(_state, _targetspeakermode, _direction, _extent, _rotation, _lowfrequencygain, _overallgain, matrixhop, _matrix) \
    (_state)->functions->pan->sumstereotosurroundmatrix(_state, _targetspeakermode, _direction, _extent, _rotation, _lowfrequencygain, _overallgain, matrixhop, _matrix)
#define FMOD_DSP_PAN_GETROLLOFFGAIN(_state, _rolloff, _distance, _mindistance, _maxdistance, _gain) \
    (_state)->functions->pan->getrolloffgain(_state, _rolloff, _distance, _mindistance, _maxdistance, _gain)


/*
[STRUCTURE] 
[
    [DESCRIPTION]
    DSP metering info used for retrieving metering info with DSP::getMeteringInfo

    [REMARKS]
    Members marked with [r] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<br>
    Members marked with [w] mean the variable can be written to.  The user can set the value.<br>

    [SEE_ALSO]
    FMOD_SPEAKER
    DSP::getMeteringInfo
]
*/
typedef struct FMOD_DSP_METERING_INFO
{
    int   numsamples;       /* [r] The number of samples considered for this metering info. */
    float peaklevel[32];    /* [r] The peak level per channel. */
    float rmslevel[32];     /* [r] The rms level per channel. */
    short numchannels;      /* [r] Number of channels. */
} FMOD_DSP_METERING_INFO;

#endif

